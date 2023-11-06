package th.mfu.service;

import th.mfu.model.*;
import th.mfu.model.interfaces.*;
import th.mfu.repository.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {
    private String secretKey = "root";

    @Autowired
    private StudentRepository StudentRepo;

    @Autowired
    private LecturerRepository LecturerRepo;

    @Autowired
    private AdminRepository AdminRepo;

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
        System.out.println(user);
        return (user != null) && (user.getPassword().equals(password));
    }

    public String GenerateJwtToken(String userid) {
        // Create a JWT token
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (1000 * (60 * 15))); // 1 hour expiration
        return Jwts.builder()
            .setSubject(userid) 
            .setExpiration(expiration) // adding expdate as salt to perform good security.
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) 
            .compact();
    }

    public User VerifyJwtToken(HttpServletRequest request) {
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
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
                Long userid = Long.parseLong(cookie_data.getSubject());
                Date now = new Date();
                Date expired = cookie_data.getExpiration();
                if (now.getTime() <= expired.getTime()) {
                    return FindByUserid(userid);
                }
            }
        } catch(Exception e) {}
        return null;
    }
}
