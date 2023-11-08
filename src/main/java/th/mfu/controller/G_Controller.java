package th.mfu.controller;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
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

    @Scheduled(fixedRate = 500) // unit is milliseconds
    @GetMapping(value="/updator")
    public void webUpdator() {
        System.out.println("Hello!!!");
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
        User Myself = userService.VerifyJwtToken(request);
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
