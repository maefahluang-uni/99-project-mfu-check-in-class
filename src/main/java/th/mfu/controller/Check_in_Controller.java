package th.mfu.controller;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

// global-attribute: usertype, userdata (can see in all path // page)
@Controller
public class Check_in_Controller {
    @Autowired
    private CourseSectionRepository CourseSectionRepo;

    @GetMapping("/check-in")
    public String CheckInPage(Model model, HttpServletResponse response, HttpServletRequest request) {
        User Myself = (User) request.getAttribute("userdata");
        if (Myself.getRole() == "STUDENT") {
            List<CourseSection> CourseCollection = CourseSectionRepo.findByStudentID(Myself.getID()); // it's a clone instance not effect direct to real entity
            for (CourseSection v0 : CourseCollection) { // prevent leak password on User Entity
                for (Student v1 : v0.student) { v1.setPassword("FORBIDDEN"); }
            }
            model.addAttribute("mycourse", CourseCollection);
        } else if (Myself.getRole() == "LECTURER") {
            List<CourseSection> CourseCollection = CourseSectionRepo.findByLecturerID(Myself.getID()); // it's a clone instance not effect direct to real entity
            System.out.println(CourseCollection.toString());
            for (CourseSection v0 : CourseCollection) { // prevent leak password on User Entity
                for (Lecturer v1 : v0.lecturer) { v1.setPassword("FORBIDDEN"); }
            }
            model.addAttribute("mycourse", CourseCollection);
        }
        return "Check-in";
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
                    if (v.getID().longValue() == lecturer.getID().longValue()) {
                        grantedAccess = true;
                        break;
                    }
                }
                if (grantedAccess) {
                    model.addAttribute("semester", Subject.semester);
                    model.addAttribute("attendanceHistory", Subject.getAttendanceHistory());
                }
            }
        }
        return "Check-in(View)";
    }
}