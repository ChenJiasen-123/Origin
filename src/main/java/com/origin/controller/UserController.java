package com.origin.controller;

import com.origin.Response;
import com.origin.dto.UserDTO;
import com.origin.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

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
