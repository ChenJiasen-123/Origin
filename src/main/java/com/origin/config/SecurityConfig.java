package com.origin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 必须禁用 CSRF，否则 POST/PUT 请求会被 403 拒绝
                .csrf(csrf -> csrf.disable())

                // 2. 启用跨域支持（配合你的 WebConfig）
                .cors(Customizer.withDefaults())

                // 3. 配置路径访问规则
                .authorizeHttpRequests(auth -> auth
                        // 放行注册、登录接口，以及你目前正在测试的接口
                        .requestMatchers("/user/register", "/user/login", "/user/sendCode", "/schedule/**", "/api/**", "/friend/**").permitAll()
                        // 其余所有请求暂时也放行（开发阶段方便调试，后期再改回来）
                        .anyRequest().permitAll()
                )

                // 4. 禁用掉那个跳出来的默认登录页面
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
