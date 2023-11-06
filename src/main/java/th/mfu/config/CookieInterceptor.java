package th.mfu.config;

import th.mfu.model.*;
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
        Object Myself = userService.VerifyJwtToken(request);
        boolean InvaildAccessToken = (Myself == null);
        if (InvaildAccessToken) {
            Cookie accessToken = new Cookie("accessToken", null);
            accessToken.setMaxAge(0);
            response.addCookie(accessToken);
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
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}
}
