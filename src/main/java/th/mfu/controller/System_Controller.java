package th.mfu.controller;

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;
import org.springframework.web.socket.WebSocketSession;

import th.mfu.model.*;
import th.mfu.model.interfaces.*;
import th.mfu.service.*;
import th.mfu.repository.*;

import org.springframework.util.StreamUtils;
import org.springframework.core.io.Resource;
import java.time.DayOfWeek;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

// global-attribute: usertype, userdata (can see in all path // page)
@EnableScheduling
@Controller
public class System_Controller {
    @Autowired
    private UserService userService;
    @Autowired
    private DateUtils DateService;

    @Autowired
    private StudentRepository StudentRepo;
    @Autowired
    private LecturerRepository LecturerRepo;
    @Autowired
    private CourseRepository CourseRepo;
    @Autowired
    private CourseSectionRepository CourseSectionRepo;

    // Unfortunately websocket can't be use on google cloud build since it optimize for non-always-live server
    // private final SimpMessagingTemplate MessagingService;
    // public G_Controller(SimpMessagingTemplate MessagingTemplate, UserService userService) {
    //     this.MessagingService = MessagingTemplate;
    // }

    private final int KEYSYNC_FIXED_LENGTH = 8; // BASE64URL version for safe GET method (64 ^ length) = space collision probability
    private final int GATE_AUTO_TIMEOUT = (60 * 1) * 1000; // TimeUnit.MILLISECONDS (1 minutes)
    private final int QR_CODE_AUTO_TIMEOUT = 5; // // TimeUnit.SECONDS (5 seconds is best option balance between real student in the class scan the qr and prevent some student send qr to others.)
    private final int GENERATE_PER_MILLISECONDS = 500;

    private Cache<Long, Date> AUTHENTICATION_GATE = CacheBuilder.newBuilder().expireAfterWrite(GATE_AUTO_TIMEOUT, TimeUnit.MILLISECONDS).build();
    private Cache<String, Long> AUTHENTICATION_KEY = CacheBuilder.newBuilder().expireAfterWrite(QR_CODE_AUTO_TIMEOUT, TimeUnit.SECONDS).build();
    private Cache<Long, String> RECENT_KEY = CacheBuilder.newBuilder().expireAfterWrite(QR_CODE_AUTO_TIMEOUT, TimeUnit.SECONDS).build();

    @Scheduled(fixedRate = GENERATE_PER_MILLISECONDS)
    public void QRAuthUpdator() {
        for (Map.Entry<Long, Date> entry : AUTHENTICATION_GATE.asMap().entrySet()) {
            Long SUBJECT_ID = entry.getKey();
            String KEYSYNC = userService.GenerateBase64UrlToken(KEYSYNC_FIXED_LENGTH);
            RECENT_KEY.put(SUBJECT_ID, KEYSYNC);
            AUTHENTICATION_KEY.put(KEYSYNC, SUBJECT_ID);
        }
    }
    
    @GetMapping("/scan")
    public String ScanPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        return "Scan";
    }

    @PostMapping("/qr")
    public ResponseEntity<HashMap<String, Object>> QRCodeGenerator(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam Long instanceid) {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        HashMap<Long, HashMap<Long, Boolean>> HISTORY = (Subject != null) ? Subject.getAttendanceHistory() : null;
        Date GATE = AUTHENTICATION_GATE.getIfPresent(instanceid);
        if (GATE != null) {
            String QR_AUTHENTICATION_KEY = RECENT_KEY.getIfPresent(instanceid);
            if (QR_AUTHENTICATION_KEY == null) { // incase gate doesnt timeout and no recent key
                String KEYSYNC = userService.GenerateBase64UrlToken(KEYSYNC_FIXED_LENGTH);
                RECENT_KEY.put(instanceid, KEYSYNC);
                AUTHENTICATION_KEY.put(KEYSYNC, instanceid);
                QR_AUTHENTICATION_KEY = KEYSYNC;
            }
            String FINAL_OUTPUT = QR_AUTHENTICATION_KEY;
            return ResponseEntity.status(HttpStatus.OK)
                .body(new HashMap<String, Object>() {{
                    put("success", true);
                    put("mygate", (GATE.getTime() + GATE_AUTO_TIMEOUT));
                    put("history", HISTORY);
                    put("value", FINAL_OUTPUT);
                }});
        }
        return ResponseEntity.status(HttpStatus.OK)
            .body(new HashMap<String, Object>() {{
                put("success", false);
                put("history", HISTORY);
                put("message", "AUTHENTICATION_GATE was timeout, try to reopen again.");
            }});
    }

    //http://localhost:8100/qr?instanceid=10000
    @GetMapping("/qr") // for LECTURER open qrcode to check-in class (require instanceid: (CourseSection ID))
    public String QRCodePage(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam Long instanceid) throws Exception {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        if (Subject != null) {
            Long SUBJECT_ID = Subject.getID();
            User Myself = (User) request.getAttribute("userdata");
            if (Myself instanceof Lecturer) {
                Lecturer lecturer = (Lecturer) Myself;
                boolean grantedAccess = false;
                for (Lecturer v : Subject.lecturer) {
                    if (v.getID().longValue() == lecturer.getID().longValue()) { // Long.longValue() turn Object type to primitive type so then can compare.
                        grantedAccess = true;
                        break;
                    }
                }
                if (grantedAccess) {
                    String QR_AUTHENTICATION_KEY = RECENT_KEY.getIfPresent(SUBJECT_ID);
                    if (QR_AUTHENTICATION_KEY == null) {
                        Date CanAuthentication = AUTHENTICATION_GATE.getIfPresent(SUBJECT_ID);
                        if (CanAuthentication != null) { // first init
                            String KEYSYNC = userService.GenerateBase64UrlToken(KEYSYNC_FIXED_LENGTH);
                            RECENT_KEY.put(SUBJECT_ID, KEYSYNC);
                            AUTHENTICATION_KEY.put(KEYSYNC, SUBJECT_ID);
                        }
                    }
                    // set this subject as gate qr code open.
                    AUTHENTICATION_GATE.put(Subject.getID(), new Date()); // 15 minute automaticly close (need to reopen for auth.)
                    model.addAttribute("subject", Subject); // permission: allow
                    model.addAttribute("rate", GENERATE_PER_MILLISECONDS);
                    return "QR";
                }
            }
            model.addAttribute("subject", null); // permission: denied
            return "QR"; 
        } else {
            throw new Exception(HttpStatus.NOT_FOUND + ": subject in database.");
        }
    }



    @GetMapping("/qr/{key}") // for STUDENT scan qrcode to check-in class by key
    public String QRCodeScan(Model model, HttpServletResponse response, HttpServletRequest request, @PathVariable String key) {
        String KEYSYNC = key;
        Long SUBJECT_ID = AUTHENTICATION_KEY.getIfPresent(KEYSYNC);
        if (SUBJECT_ID != null) { // global-key verified.
            CourseSection Subject = CourseSectionRepo.findByID(SUBJECT_ID);
            if (Subject != null) {
                User Myself = (User) request.getAttribute("userdata");
                if (Myself instanceof Student) {
                    Student student = (Student) Myself;
                    boolean isMember = false;
                    for (Student v : Subject.student) {
                        if (v.getID().longValue() == student.getID().longValue()) { // Long.longValue() turn Object type to primitive type so then can compare.
                            isMember = true;
                            break;
                        }
                    }
                    if (isMember) { // check if in student in this class
                        // so in database sql we use period ref as in thailand if it run on localhost it will use "Asia/Bangkok" but if it run on gcloud it will use "Etc/UTC" instead.
                        // so then wee need to set zone as thailand.
                        ZoneId ReferenceZone = ZoneId.of("Asia/Bangkok"); // ZoneId.systemDefault();
                        LocalDate Now = LocalDate.now(ReferenceZone);
                        String[] GP_C = Subject.getPeriod().split(", ");
                        String AbbreviatedDayLabel = GP_C[0];
                        String DayLabelAbbreviation = Now.format(DateTimeFormatter.ofPattern("E")); // "E" for day abbreviation
                        // model.addAttribute("DEBUG_Z", ReferenceZone);
                        // model.addAttribute("DEBUG_NOW", Now); // just test to see if it affect on gcloud?
                        // model.addAttribute("DEBUG_DAYLABEL_1", AbbreviatedDayLabel); // just test to see if it affect on gcloud?
                        // model.addAttribute("DEBUG_DAYLABEL_2", DayLabelAbbreviation); // just test to see if it affect on gcloud?
                        if (DayLabelAbbreviation.equals(AbbreviatedDayLabel)) { // check if in same daylabel
                            String[] ST_C = Subject.semester.getDateStart().split("/"); // MM/dd/yyyy
                            int Month = Integer.parseInt(ST_C[0]);
                            int Day = Integer.parseInt(ST_C[1]);
                            int Year = Integer.parseInt(ST_C[2]);
                            LocalDate StartDate = LocalDate.of(Year, Month, Day);
                            Long WeekSemester = ChronoUnit.WEEKS.between(StartDate, Now); // cut week every wed (DateStart is wed according to calendar)
                            Subject.markAttendance(student.getID(), WeekSemester, true);
                            CourseSectionRepo.save(Subject);
                            model.addAttribute("success", true);
                            model.addAttribute("message", Now);
                        } else {
                            model.addAttribute("success", false);
                            model.addAttribute("message", "You are only allow for authentication in a class on the day.");
                        }
                    } else {
                        model.addAttribute("success", false);
                        model.addAttribute("message", "You are not in this class.");
                    }
                } else {
                    model.addAttribute("success", false);
                    model.addAttribute("message", "Student role only allow for the authentication.");
                }
            } else {
                model.addAttribute("success", false);
                model.addAttribute("message", "Subject not found qr pointer is null.");
            }
        } else {
            model.addAttribute("success", false);
            model.addAttribute("message", "QRCode authentication is invaild / expired, please try rescan.");
        }
        return "QR_Response";
    }

    @GetMapping("/home")
    public String HomePage(Model model, HttpServletResponse response, HttpServletRequest request) {
        // List<StudentSchedule> items = StudentScheduleRepo.findByStudentID(6531503070L);
        // for (StudentSchedule item : items) {
        //     System.out.println(item.getCourseSection().getCourse());
        // }
        // CourseSection sec = CourseSectionRepo.findByID(10000L);
        // for (Student item : sec.student) {
        //     System.out.println(item.getID());
        // }
        // List<CourseSection> items = CourseSectionRepo.findByStudentIDAndSemesterID(6531503070L, 6666L);
        // for (CourseSection item : items) {
        //     System.out.println(item);
        // }
        return "HomeV2";
    }

    @GetMapping("/login")
    public String LoginPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        return "LoginV2";
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, Object>> Login(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam String userid, @RequestParam String password) {
        if (userService.Authenticate(userid, password)) {
            // User authenticated successfully, redirect to the home page
            String jwtToken = userService.GenerateJwtToken(userid);
            Cookie cookie = new Cookie("accessToken", jwtToken);
            cookie.setMaxAge(60 * 15); // unit: seconds
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.OK)
                .body(new HashMap<String, Object>() {{
                    put("redirect", "/home");
                }});
        } else {
            // Authentication failed, show an error message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap<String, Object>() {{
                    put("success", "false");
                    put("message", "Incorrect username or password.");
                }});
        }
    }

    @PostMapping("/register")
    public ResponseEntity<HashMap<String, Object>> register(Model model, HttpServletResponse response, HttpServletRequest request,
        @RequestParam String usertype) {
            /*
             * http://localhost:8100/register?usertype=LECTURER
             * {
             *  USER_ID: 1111
             *  PASSWORD: "1234"
             *  ...
             *  School: "abc"
             * }
             */
        User Myself = userService.VerifyJwtToken(request, response);
        if (Myself.getRole() == "ADMIN") { // or using "Myself instanceof Admin"
            if (usertype == "STUDENT") {
                
            } else if (usertype == "LECTURER") {
                // Get form-data
                String USER_ID = request.getParameter("USER_ID");
                String PASSWORD = request.getParameter("PASSWORD");
                String NAME = request.getParameter("NAME");
                String Department = request.getParameter("Department");
                String School = request.getParameter("School");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        } else {
            // Authentication failed, show an error message
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap<String, Object>() {{
                    put("success", "false");
                    put("message", "AUTHORIZATION failed please try relogin.");
                }});
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        return "redirect:/login";
    }
    @GetMapping("/manage-course")
    public String viewCourses(Model model) {
        List<Course> courses = (List<Course>) CourseRepo.findAll();
        model.addAttribute("courses", courses);
        return "Manage-course";
    }
    @PostMapping("/add-course")
    public ResponseEntity<HashMap<String, Object>> addCourse(@RequestParam String courseName) {
        if (courseName != null && !courseName.trim().isEmpty()) {
            if (!CourseRepo.existsByNameIgnoreCase(courseName)) {
                Course newCourse = new Course();
                newCourse.setName(courseName);
                CourseRepo.save(newCourse);
                List<Course> courses = (List<Course>) CourseRepo.findAll();
                return ResponseEntity.status(HttpStatus.OK)
                    .body(new HashMap<String, Object>() {{
                        put("success", true);
                        put("message", "Course added successfully.");
                        put("courses", courses);
                    }});
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new HashMap<String, Object>() {{
                        put("success", false);
                        put("message", "Course with the same name already exists.");
                    }});
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "Please enter a course name.");
                }});
        }
    }
}
