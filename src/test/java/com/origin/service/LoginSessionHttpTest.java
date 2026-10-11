package com.origin.service;

import com.origin.controller.UserController;
import com.origin.entity.User;
import com.origin.entity.UserRepository;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = LoginSessionHttpTest.TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"server.servlet.session.store-dir=target/test-sessions"})
class LoginSessionHttpTest {
    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
        SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
    @Import({UserController.class, UserServiceImpl.class})
    static class TestApplication {
        @Bean UserRepository repository() { return mock(UserRepository.class); }
        @Bean PasswordEncoder passwordEncoder() { return mock(PasswordEncoder.class); }
        @Bean VerificationCodeService verificationCodeService() { return mock(VerificationCodeService.class); }
        @Bean EmailService emailService() { return mock(EmailService.class); }
    }
    @Autowired ServletWebServerApplicationContext application;
    @Autowired UserRepository repository;
    @Autowired PasswordEncoder encoder;

    @Test void loginSurvivesThirtyOneMinutesIdleAndLogoutRevokesIt() throws Exception {
        User user = new User();
        user.setId(7L);
        user.setEmail("session-test@example.com");
        user.setPassword("encoded");
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(encoder.matches("password", "encoded")).thenReturn(true);
        String base = "http://localhost:" + application.getWebServer().getPort();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> login = client.send(HttpRequest.newBuilder(URI.create(base + "/user/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"session-test@example.com\",\"password\":\"password\"}"))
            .build(), HttpResponse.BodyHandlers.ofString());
        assertTrue(login.body().contains("\"success\":true"), login.body());
        String setCookie = login.headers().firstValue("set-cookie").orElseThrow();
        assertTrue(setCookie.contains("Max-Age=315360000"), setCookie);
        String cookie = setCookie.split(";",2)[0];
        String sessionId = cookie.substring(cookie.indexOf('=')+1);
        TomcatWebServer server = (TomcatWebServer) application.getWebServer();
        Context context = (Context) server.getTomcat().getHost().findChildren()[0];
        Manager manager = context.getManager();
        Session session = manager.findSession(sessionId);
        assertNotNull(session);
        assertEquals(-1, session.getMaxInactiveInterval());
        long idleTime = System.currentTimeMillis() - 31L * 60 * 1000;
        ReflectionTestUtils.setField(session, "lastAccessedTime", idleTime);
        ReflectionTestUtils.setField(session, "thisAccessedTime", idleTime);
        manager.backgroundProcess();
        HttpResponse<String> me = client.send(HttpRequest.newBuilder(URI.create(base+"/user/me"))
            .header("Cookie",cookie).GET().build(),HttpResponse.BodyHandlers.ofString());
        assertTrue(me.body().contains("\"success\":true"), me.body());
        client.send(HttpRequest.newBuilder(URI.create(base+"/user/logout")).header("Cookie",cookie)
            .POST(HttpRequest.BodyPublishers.noBody()).build(),HttpResponse.BodyHandlers.ofString());
        assertNull(manager.findSession(sessionId));
        HttpResponse<String> afterLogout = client.send(HttpRequest.newBuilder(URI.create(base+"/user/me"))
            .header("Cookie",cookie).GET().build(),HttpResponse.BodyHandlers.ofString());
        assertTrue(afterLogout.body().contains("\"success\":false"), afterLogout.body());
    }
}
