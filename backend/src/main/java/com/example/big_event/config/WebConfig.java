package com.example.big_event.config;

import com.example.big_event.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录接口和注册接口不拦截
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/user/login", "/user/register","/upload/**");
    }

    // 本地真实存放路径
    @Value("${real.path}")
    private String realPath;
    // 浏览器访问前缀（自定义）
    @Value("${web.prefix}")
    private String webPrefix;

    @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // 含义：访问 /upload/** 就映射到本地 E:/xxx/fileStore/
            registry.addResourceHandler(webPrefix + "**")
                .addResourceLocations("file:" + realPath);
    }
}
