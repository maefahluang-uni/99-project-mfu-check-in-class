package th.mfu.service;

import th.mfu.model.*;
import th.mfu.model.interfaces.*;
import th.mfu.repository.*;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.SecureRandom;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;

@Service
public class UserService {
    private final String SECRET_KEY = "root";
    private final int REMEBER_ME_LIFETIME = (60 * 15) * 1000; // 15 minutes if user didn't load any page in x minutes it'll logout user automaticly 

    @Autowired
    private StudentRepository StudentRepo;
    @Autowired
    private LecturerRepository LecturerRepo;
    @Autowired
    private AdminRepository AdminRepo;

    @Autowired
    private CourseSectionRepository CourseSectionRepo;

    public User FindByUserid(Long userid) {
        User student = StudentRepo.findByID(userid);
        User lecturer = LecturerRepo.findByID(userid);
        User admin = AdminRepo.findByID(userid);
        if (student != null) {
            return student;
        } else if (lecturer != null) {
            return lecturer;
        } else if (admin != null) {
            return admin;
        }
        return null;
    }

    public boolean Authenticate(String userid, String password) {
        Long __userid__ = Long.valueOf(userid);
        User user = FindByUserid(__userid__);
        String hashedPassword = user.getPassword();
        // System.out.println(String.format("hashedPassword: %s", BCrypt.hashpw(password, BCrypt.gensalt()))); // gensalt round default is 10 this method can make password more secure by hashing + salt with multiple time hash. prevent attack from like rainbow table
        if (user != null) {
            if (BCrypt.checkpw(password, hashedPassword)) {
                return true;
            } 
        }
        return false;
    }

    // @Transactional
    // public List<Student> getStudentsByLecturer(Long lecturerId) {
    //     Lecturer lecturer = LecturerRepo.findByID(lecturerId);
    //     // Access students within the transactional method
    //     return lecturer.getStudents();
    // }

    public String GenerateBase64UrlToken(int Length) {
        byte[] randomBytes = new byte[Length];
        new SecureRandom().nextBytes(randomBytes);
        String base64Url = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return base64Url.substring(0, Length);
    }
    
    public String GenerateJwtToken(String userid) {
        // Create a JWT token
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REMEBER_ME_LIFETIME);
        return Jwts.builder()
            .setSubject(userid) 
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()) 
            .compact();
    }

    public User VerifyJwtToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        HashMap<String, Cookie> cookieMap = new HashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        try {
            Cookie accessToken = cookieMap.getOrDefault("accessToken", null);
            if (accessToken != null) { // if still has cookie in chrome
                String token = accessToken.getValue();
                Claims cookie_data = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes()) // verify if it from system generated or not.
                    .parseClaimsJws(token)
                    .getBody();
                Long userid = Long.parseLong(cookie_data.getSubject());
                Date now = new Date();
                Date expired = cookie_data.getExpiration();
                if (now.getTime() <= expired.getTime()) {
                    User _user_ = FindByUserid(userid);
                    if (_user_ != null) {
                        accessToken.setValue(GenerateJwtToken(userid.toString()));
                        response.addCookie(accessToken);
                        return _user_;
                    }
                }
            }
        } catch(Exception e) {}
        return null;
    }

    public List<CourseSection> MyCourse(User Myself) {
        List<CourseSection> CourseCollection = null;
        if (Myself.getRole() == "STUDENT") { CourseCollection = CourseSectionRepo.findByStudentID(Myself.getID()); } // it's a clone instance not effect direct to real entity
        else if (Myself.getRole() == "LECTURER") { CourseCollection = CourseSectionRepo.findByLecturerID(Myself.getID()); } // it's a clone instance not effect direct to real entity
        for (CourseSection v0 : CourseCollection) { // prevent leak password on User Entity
            for (Student v1 : v0.student) { v1.setPassword("FORBIDDEN"); }
            for (Lecturer v2 : v0.lecturer) { v2.setPassword("FORBIDDEN"); }
        }
        return CourseCollection;
    }
}
