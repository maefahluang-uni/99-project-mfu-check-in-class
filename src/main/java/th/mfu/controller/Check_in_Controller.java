package th.mfu.controller;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.servlet.http.*;
import javax.transaction.Transactional;

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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// global-attribute: usertype, userdata (can see in all path // page)
@Controller
public class Check_in_Controller {
    @Autowired
    private UserService userService;
    @Autowired
    private DateUtils DateService;

    @Autowired
    private StudentRepository StudentRepo;
    @Autowired
    private CourseSectionRepository CourseSectionRepo;
    @Autowired
    private SemesterRepository SemesterRepo;

    @GetMapping("/check-in")
    public String CheckInPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        User Myself = (User) request.getAttribute("userdata");
        // if (Myself instanceof Lecturer) {
        //     System.out.println("XXXXXDDDDD");
        //     System.out.println(userService.getStudentsByLecturer(1150L));
        //     System.out.println("XXXXXDDDDD");
        // }
        model.addAttribute("mycourse", userService.MyCourse(Myself)); // can be null in not role student or lecturer
        model.addAttribute("semester", SemesterRepo.findAll());
        return String.format("[%s] Check-in", Myself.getRole());
    }
    
    @GetMapping("/check-in/view")
    public String ViewStudentInCourse(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam Long instanceid) {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        if (Subject != null) {
            User Myself = (User) request.getAttribute("userdata");
            if (Myself instanceof Lecturer) {
                Lecturer lecturer = (Lecturer) Myself;
                boolean grantedAccess = false;
                for (Lecturer v : Subject.lecturer) {
                    v.setPassword("FORBIDDEN");
                    if (v.getID().longValue() == lecturer.getID().longValue()) {
                        grantedAccess = true;
                        break;
                    }
                }
                for (Student v : Subject.student) { v.setPassword("FORBIDDEN"); }
                if (grantedAccess) {
                    model.addAttribute("subject", Subject);
                    // model.addAttribute("students", Subject.student);
                    // model.addAttribute("semester", Subject.semester);
                    // model.addAttribute("history", Subject.getAttendanceHistory());
                    // model.addAttribute("period", Subject.getPeriod());
                }
                return "[LECTURER] Check-in(View)";
            }
        }
        return null;
    }

    @PutMapping("/check-in/edit")
    public ResponseEntity<HashMap<String, Object>> EditStudentCheckIn(
        Model model, HttpServletResponse response, HttpServletRequest request,
        @RequestParam Long week, @RequestParam Long instanceid, @RequestParam Long studentid, @RequestParam Boolean value
    ) {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        if (Subject != null) {
            User Myself = (User) request.getAttribute("userdata");
            if (Myself instanceof Lecturer) {
                Lecturer lecturer = (Lecturer) Myself;
                boolean grantedAccess = false;
                for (Lecturer v : Subject.lecturer) {
                    if (v.getID().longValue() == lecturer.getID().longValue()) {    
                        grantedAccess = true;
                        break;
                    }
                }
                if (grantedAccess) {
                    try {
                        String[] GP_C = Subject.getPeriod().split(", ");
                        String AbbreviatedDayLabel = GP_C[0];
                        Date RecordDate = DateService.getDateByWeekAndDayLabel(week, AbbreviatedDayLabel, Subject.semester.getDateStart(), Subject.semester.getDateFinish());
                        if (RecordDate != null) {
                            Subject.markAttendance(studentid, week, value);
                            CourseSectionRepo.save(Subject);
                        } else {
                            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                                .body(new HashMap<String, Object>() {{
                                    put("success", false);
                                    put("message", "you allow for set student check within week of semester.");
                                }});
                        }
                    } catch(Exception e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new HashMap<String, Object>() {{
                                put("success", false);
                                put("message", "search from query result is not found.");
                            }});
                    }
                    return ResponseEntity.status(HttpStatus.OK)
                        .body(new HashMap<String, Object>() {{
                            put("success", true);
                            put("check", Subject.getAttendance(studentid, week));
                            put("map", Subject.getAttendanceHistory());
                        }});
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "unable to edit permission denied.");
            }});
    }

    @GetMapping("/check-in/edit/student")
    public String EditStudentInCourse(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam Long instanceid) {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        if (Subject != null) {
            User Myself = (User) request.getAttribute("userdata");
            if (Myself instanceof Lecturer) {
                Lecturer lecturer = (Lecturer) Myself;
                boolean grantedAccess = false;
                for (Student v : Subject.student) { v.setPassword("FORBIDDEN"); }
                for (Lecturer v : Subject.lecturer) {
                    v.setPassword("FORBIDDEN");
                    if (v.getID().longValue() == lecturer.getID().longValue()) {
                        grantedAccess = true;
                        break;
                    }
                }
                if (grantedAccess) {
                    model.addAttribute("subject", Subject);
                }
                return "[LECTURER] Check-in(Edit)";
            }
        }
        return null;
    }

    @GetMapping("/test")
    public ResponseEntity<HashMap<String, Object>> Test(@RequestParam Long instanceid) {
        CourseSection Subject = CourseSectionRepo.findByID(instanceid);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new HashMap<String, Object>() {{
                put("DATA", Subject.lecturer);
            }});    
    }

    @GetMapping("/check-in/edit/student/{studentId}/unlink")
    public ResponseEntity<HashMap<String, Object>> UnlinkCourseStudent(Model model, @PathVariable Long studentId, @RequestParam Long instanceid) {
        Student student = StudentRepo.findByID(studentId);
        if (student != null) {
            List<CourseSection> studentSections = CourseSectionRepo.findByStudentID(studentId);
            CourseSection subject = null;
            for (CourseSection v1 : studentSections) {
                if (v1.getID().longValue() == instanceid) {
                    subject = v1;
                    break;
                }
            }
            if (subject != null) {
                boolean IsRemoveSuccess = subject.student.remove(student);
                CourseSectionRepo.save(subject);
                return ResponseEntity.status(HttpStatus.OK)
                    .body(new HashMap<String, Object>() {{
                        put("success", true);
                    }});
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new HashMap<String, Object>() {{
                        put("success", false);
                        put("message", "Student isn't in this course already.");
                    }});
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Student not found.");
            }});
    }
    @GetMapping("/check-in/edit/student/add")
    public String editStudentAddInCourse(Model model, HttpServletResponse response, HttpServletRequest request, @RequestParam Long instanceid) {
        CourseSection subject = CourseSectionRepo.findByID(instanceid);

        if (subject != null) {
            User myself = (User) request.getAttribute("userdata");

            if (myself instanceof Lecturer) {
                Lecturer lecturer = (Lecturer) myself;
                boolean grantedAccess = false;

                for (Lecturer v : subject.lecturer) {
                    v.setPassword("FORBIDDEN");

                    if (v.getID().equals(lecturer.getID())) {
                        grantedAccess = true;
                        break;
                    }
                }

                if (grantedAccess) {
                    // Retrieve all students from the repository
                    List<Student> allStudents = (List<Student>) StudentRepo.findAll();

                    // Create a list of students not in the subject
                    List<Student> studentsNotInSubject = allStudents.stream()
                            .filter(student -> !subject.student.contains(student))
                            .peek(student -> student.setPassword("FORBIDDEN"))
                            .collect(Collectors.toList());

                    model.addAttribute("subject", subject);
                    model.addAttribute("studentsNotInSubject", studentsNotInSubject);

                    return "[LECTURER] Check-in(Edit-Add)";
                }
            }
        }

        return null;
    }
        // ?instanceid=10000
        @GetMapping("/check-in/edit/student/{studentId}/link")
        public ResponseEntity<HashMap<String, Object>> LinkcourseStudent(Model model, @PathVariable Long studentId, @RequestParam Long instanceid) {
            Student student = StudentRepo.findByID(studentId);
            if (student != null) {
                List<CourseSection> allSections = (List<CourseSection>) CourseSectionRepo.findAll();
                List<CourseSection> studentSections = CourseSectionRepo.findByStudentID(studentId);
                allSections.removeAll(studentSections);
                CourseSection subject = null;
                for (CourseSection v1 : allSections) {
                    System.out.println(v1.getID());
                    if (v1.getID().longValue() == instanceid) {
                        subject = v1;
                        break;
                    }
                }
                if (subject != null) {
                    subject.student.add(student);
                    CourseSectionRepo.save(subject);
                    return ResponseEntity.status(HttpStatus.OK)
                        .body(new HashMap<String, Object>() {{
                            put("success", true);
                        }});
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new HashMap<String, Object>() {{
                            put("success", false);
                            put("message", "Student was already link to this course");
                        }});
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new HashMap<String, Object>() {{
                    put("success", false);
                    put("message", "Student not found.");
                }});
        }

}