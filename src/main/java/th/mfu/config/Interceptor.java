package th.mfu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Interceptor implements WebMvcConfigurer {
    @Bean
    public CookieInterceptor cookieInterceptor() { // tell spring for this is instance.
        return new CookieInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cookieInterceptor())
                .addPathPatterns("/**") // Apply the interceptor to all paths
                .excludePathPatterns("/css/**") // static folder
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/images/**");
    }
}
