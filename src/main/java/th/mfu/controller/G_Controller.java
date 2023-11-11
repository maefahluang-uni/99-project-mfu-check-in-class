package th.mfu.controller;

import java.io.*;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import th.mfu.model.*;
import th.mfu.model.interfaces.*;
import th.mfu.service.*;
import th.mfu.repository.*;

import org.springframework.util.StreamUtils;
import org.springframework.core.io.Resource;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

// global-attribute: usertype, userdata (can see in all path // page)
@EnableScheduling
@Controller
public class G_Controller {
    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository StudentRepo;

    @Autowired
    private LecturerRepository LecturerRepo;

    @Autowired
    private CourseRepository CourseRepo;

    @Autowired
    private CourseSectionRepository CourseSectionRepo;
    
    private final int KEYSYNC_FIXED_LENGTH = 2; // BASE64URL version
    private final int GATE_SAFE_TIMEOUT = 5; // TimeUnit.SECONDS
    private final int GATE_AUTO_TIMEOUT = ((60 * 10) + GATE_SAFE_TIMEOUT) * 1000; // TimeUnit.MILLISECONDS (5 minutes) ((+5 seconds safe for broadcasting state))
    private final int QR_CODE_AUTO_TIMEOUT = 5; // // TimeUnit.SECONDS (5 seconds is best option balance between real student in the class scan the qr and prevent some student send qr to others.)
    private final SimpMessagingTemplate MessagingService;
    private Cache<Long, Date> AUTHENTICATION_GATE = CacheBuilder.newBuilder().expireAfterWrite(GATE_AUTO_TIMEOUT, TimeUnit.MILLISECONDS).build();
    private Cache<String, Long> AUTHENTICATION_KEY = CacheBuilder.newBuilder().expireAfterWrite(QR_CODE_AUTO_TIMEOUT, TimeUnit.SECONDS).build();
    private Cache<Long, String> RECENT_KEY = CacheBuilder.newBuilder().expireAfterWrite(QR_CODE_AUTO_TIMEOUT, TimeUnit.SECONDS).build();
    public G_Controller(SimpMessagingTemplate MessagingTemplate, UserService userService) {
        this.MessagingService = MessagingTemplate;
    }

    @Scheduled(fixedRate = 500)
    public void QRAuthUpdator() {
        for (Map.Entry<Long, Date> entry : AUTHENTICATION_GATE.asMap().entrySet()) {
            Long SUBJECT_ID = entry.getKey();
            Date START_DATE_LISTENING = entry.getValue();
            Date NOW = new Date();
            Long SPANTIME = (NOW.getTime() - START_DATE_LISTENING.getTime());
            Long TIMEOUT = (GATE_AUTO_TIMEOUT - (GATE_SAFE_TIMEOUT * 1000L));
            if (SPANTIME >= TIMEOUT) { // last 5 seconds before memory cache is gone.
                AUTHENTICATION_GATE.invalidate(SUBJECT_ID);
                MessagingService.convertAndSend("/topic/qr-auth/" + SUBJECT_ID,
                ResponseEntity.status(HttpStatus.OK)
                    .body(new HashMap<String, Object>() {{
                        put("success", false);
                        put("message", "AUTHENTICATION_GATE was timeout, try to reopen again.");
                    }}));
                continue;
            }
            String KEYSYNC = userService.GenerateBase64UrlToken(KEYSYNC_FIXED_LENGTH);
            RECENT_KEY.put(SUBJECT_ID, KEYSYNC);
            AUTHENTICATION_KEY.put(KEYSYNC, SUBJECT_ID);
            MessagingService.convertAndSend("/topic/qr-auth/" + SUBJECT_ID,
                ResponseEntity.status(HttpStatus.OK)
                    .body(new HashMap<String, Object>() {{
                        put("success", true);
                        put("gate_timeout", START_DATE_LISTENING.getTime() + TIMEOUT);
                        put("value", KEYSYNC);
                    }}));
        }
    }
    
    @MessageMapping("/request-current-qr-auth/{id}")
    @SendTo("/topic/qr-auth/{id}")
    public ResponseEntity<HashMap<String, Object>> QRAuthListener(@DestinationVariable Long id) throws InterruptedException {
        String[] recentSubjectKeyHolder = new String[1];
        recentSubjectKeyHolder[0] = RECENT_KEY.getIfPresent(id);
        if (recentSubjectKeyHolder[0] == null) {
            Date canAuthentication = AUTHENTICATION_GATE.getIfPresent(id);
            if (canAuthentication != null) {
                String keySync = userService.GenerateBase64UrlToken(KEYSYNC_FIXED_LENGTH);
                RECENT_KEY.put(id, keySync);
                AUTHENTICATION_KEY.put(keySync, id);
                recentSubjectKeyHolder[0] = keySync;
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new HashMap<String, Object>() {{
                            put("success", false);
                            put("message", "AUTHENTICATION_GATE was timeout, try to reopen again.");
                        }});
            }
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new HashMap<String, Object>() {{
                    put("success", true);
                    put("gate_timeout", (new Date().getTime()) + (GATE_AUTO_TIMEOUT - (GATE_SAFE_TIMEOUT * 1000L)));
                    put("value", recentSubjectKeyHolder[0]);
                }});
    }

    @GetMapping("/scan")
    public String ScanPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        return "Scan";
    }

    // http://localhost:8100/qr?instanceid=10000
    @GetMapping("/qr") // for LECTURER open qrcode to check-in class (require instanceid: (CourseSection ID))
    public String QRCodePage(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam Long instanceid) throws Exception {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        if (Subject != null) {
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
                    AUTHENTICATION_GATE.put(Subject.getID(), new Date()); // 15 minute automaticly close (need to reopen for auth.)
                    model.addAttribute("subject", Subject); // permission: allow
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
    public ResponseEntity<HashMap<String, Object>> QRCodeScan(Model model, HttpServletResponse response, HttpServletRequest request, @PathVariable String key) {
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
                    if (isMember) {
                        return ResponseEntity.status(HttpStatus.OK)
                            .body(new HashMap<String, Object>() {{
                                put("success", true);
                            }});
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new HashMap<String, Object>() {{
                                put("success", false);
                                put("message", "You are not in this class.");
                            }});
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "QRCode authentication is expired, please try rescan.");
            }});
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

    @GetMapping("/check-in")
    public String CheckInPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        User Myself = (User) request.getAttribute("userdata");
        if (Myself.getRole() == "STUDENT" || Myself.getRole() == "LECTURER") {
            List<CourseSection> CourseCollection = CourseSectionRepo.findByStudentID(Myself.getID()); // it's a clone instance not effect direct to real entity
            for (CourseSection v0 : CourseCollection) { // prevent leak password on User Entity
                for (Student v1 : v0.student) { v1.setPassword("FORBIDDEN"); }
                for (Lecturer v2 : v0.lecturer) { v2.setPassword("FORBIDDEN"); }
            }
            model.addAttribute("mycourse", CourseCollection);
        }
        return "Check-in";
    }
    
    @GetMapping("/course")
    public String CoursePage(Model model, HttpServletResponse response, HttpServletRequest request) {
        User Myself = (User) request.getAttribute("userdata");
        if (Myself.getRole() == "STUDENT" || Myself.getRole() == "LECTURER") {
            List<CourseSection> CourseCollection = CourseSectionRepo.findByStudentID(Myself.getID()); // it's a clone instance not effect direct to real entity
            for (CourseSection v0 : CourseCollection) { // prevent leak password on User Entity
                for (Student v1 : v0.student) { v1.setPassword("FORBIDDEN"); }
                for (Lecturer v2 : v0.lecturer) { v2.setPassword("FORBIDDEN"); }
            }
            model.addAttribute("mycourse", CourseCollection);
        }
        return "Course";
    }

    @GetMapping("/contact")
    public String ContactPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        User Myself = (User) request.getAttribute("userdata");
        List<CourseSection> CourseCollection = CourseSectionRepo.findByStudentID(Myself.getID());
        model.addAttribute("MyCourse", CourseCollection);
        return "Contact";
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
}
