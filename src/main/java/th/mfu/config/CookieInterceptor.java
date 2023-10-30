package th.mfu.config;

import th.mfu.service.UserService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletContext;

import io.jsonwebtoken.Claims;

import java.io.File;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// got @Bean so can you @Autowired now
public class CookieInterceptor implements HandlerInterceptor { 
    @Autowired
    private ServletContext servletContext;
    
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String RequestURI = request.getRequestURI();
        if (RequestURI.equals("/login")) {
            return true;
        }
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
                Claims cookie_data = userService.VerifyJwtToken(token);
                String userid = cookie_data.getSubject();
                Date now = new Date();
                Date expired = cookie_data.getExpiration();
                if (now.getTime() <= expired.getTime()) {
                    return true;
                }
            }
        } catch(Exception e) {}
        if (RequestURI != null) {
            String requestedFile = servletContext.getRealPath(RequestURI);
            if (requestedFile != null) {
                File file = new File(requestedFile);
                if (file.exists()) {
                    response.sendRedirect("/login");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}
