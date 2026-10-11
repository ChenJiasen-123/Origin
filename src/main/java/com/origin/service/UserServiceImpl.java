package com.origin.service;

import com.origin.converter.UserConverter;
import com.origin.dto.UserDTO;
import com.origin.entity.User;
import com.origin.entity.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationCodeService verificationCodeService;

    public UserDTO register(UserDTO userDTO) {
        if (userDTO == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            throw new IllegalArgumentException("邮箱或密码为空");
        }
        if (userDTO.getCode() == null || !verificationCodeService.verify(userDTO.getEmail(), userDTO.getCode())) {
            throw new IllegalStateException("邮箱验证码错误或已过期");
        }
        if (!userDTO.getPassword().matches("^[A-Za-z0-9]+$")) {
            throw new IllegalStateException("密码仅允许大小写字母和数字");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("邮箱已存在");
        }
        User user = UserConverter.convertToUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        User saved = userRepository.save(user);
        return UserConverter.convertToUserDTO(saved);
    }

    public UserDTO login(String email, String rawPassword, HttpSession session) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("用户不存在"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalStateException("密码错误");
        }
        session.setMaxInactiveInterval(-1); // Keep authenticated until explicit logout.
        session.setAttribute("USER_ID", user.getId());
        return UserConverter.convertToUserDTO(user);
    }

    public UserDTO me(HttpSession session) {
        Object uid = session.getAttribute("USER_ID");
        if (uid == null) {
            return null;
        }
        User user = userRepository.findById((Long) uid).orElse(null);
        if (user == null) return null;
        return UserConverter.convertToUserDTO(user);
    }
}
