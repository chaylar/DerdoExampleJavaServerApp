package org.derdoapp.ServerConfig;

import org.derdoapp.Interceptor.BaseControllerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class BaseInterceptorAppConfig implements WebMvcConfigurer {

    @Autowired
    BaseControllerInterceptor baseControllerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseControllerInterceptor).addPathPatterns("/match/**", "/message/**", "/profile/**", "/settings/**");
    }
}
