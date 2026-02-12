package com.origin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许前端跨域访问所有接口
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                //.allowedOrigins("http://192.168.124.8:5173","http://localhost:5173") // 允许 Vue 开发服务器访问
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}