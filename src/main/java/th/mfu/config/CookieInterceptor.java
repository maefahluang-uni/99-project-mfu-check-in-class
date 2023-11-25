package th.mfu.config;

import th.mfu.model.*;
import th.mfu.model.interfaces.*;
import th.mfu.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// got @Bean so can you @Autowired now
public class CookieInterceptor implements HandlerInterceptor { 
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String RequestURI = request.getRequestURI();
        User Myself = userService.VerifyJwtToken(request, response);
        boolean InvaildAccessToken = (Myself == null);
        if (InvaildAccessToken) {
            Cookie accessToken = new Cookie("accessToken", null);
            accessToken.setMaxAge(0);
            response.addCookie(accessToken);
        } else {
            // set as global attributes.
            Myself.setPassword(null); // hide password before send to frontend
            if (Myself instanceof Student) {
                Student STUDENT = (Student) Myself;
                request.setAttribute("userdata", STUDENT);
            } else if (Myself instanceof Lecturer) {
                Lecturer LECTURER = (Lecturer) Myself;
                request.setAttribute("userdata", LECTURER);
            } else if (Myself instanceof Admin) {
                Admin ADMIN = (Admin) Myself;
                request.setAttribute("userdata", ADMIN);
            }
        }
        if (RequestURI != null) {
            if (RequestURI.equals("/login") && !InvaildAccessToken) {
                response.sendRedirect("/home");
            } else if (!RequestURI.equals("/login") && InvaildAccessToken) {
                response.sendRedirect("/login");
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        /* if (modelAndView != null) {
            modelAndView.addObject("usertype", request.getAttribute("usertype"));
            modelAndView.addObject("userdata", request.getAttribute("userdata"));
        } */
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}
