package th.mfu.service;

import th.mfu.model.*;
import th.mfu.repository.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

@Service
public class UserService {
    private String secretKey = "root";

    @Autowired
    private StudentRepository studentRepository;

    public Student FindByUserid(String userid) {
        return studentRepository.findByID(Long.valueOf(userid));
    }

    public boolean Authenticate(String userid, String password) {
        Student user = FindByUserid(userid);
        return user != null && user.getPassword().equals(password);
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

    public Claims VerifyJwtToken(String jwt) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey.getBytes())
            .parseClaimsJws(jwt)
            .getBody();
        return claims;
    }
}
