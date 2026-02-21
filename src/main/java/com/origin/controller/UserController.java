package com.origin.controller;

import com.origin.Response;
import com.origin.dto.UserDTO;
import com.origin.service.UserServiceImpl;
import com.origin.service.VerificationCodeService;
import com.origin.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/sendCode")
    public Response<String> sendCode(@RequestBody UserDTO userDTO) {
        String email = userDTO.getEmail();
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return Response.newFail("邮箱格式不合法");
        }
        String code = verificationCodeService.generate(email);
        try {
            emailService.sendVerificationCode(email, code);
        } catch (Exception ex) {
            return Response.newFail("发送验证码失败，请检查邮件配置");
        }
        return Response.newSuccess("验证码已发送，请检查邮箱");
    }

    @PostMapping("/register")
    public Response<UserDTO> register(@RequestBody UserDTO userDTO) {
        try {
            UserDTO dto = userService.register(userDTO);
            return Response.newSuccess(dto);
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Response<UserDTO> login(@RequestBody UserDTO userDTO, HttpSession session) {
        try {
            UserDTO dto = userService.login(userDTO.getEmail(), userDTO.getPassword(), session);
            return Response.newSuccess(dto);
        } catch (Exception e) {
            return Response.newFail(e.getMessage());
        }
    }

    @GetMapping("/me")
    public Response<UserDTO> me(HttpSession session) {
        UserDTO dto = userService.me(session);
        if (dto == null) {
            return Response.newFail("未登录");
        }
        return Response.newSuccess(dto);
    }

    @PostMapping("/logout")
    public Response<Void> logout(HttpSession session) {
        session.invalidate();
        return Response.newSuccess(null);
    }
}
