package com.origin.service;

import com.origin.entity.User;
import com.origin.entity.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoginSessionTest {
    @Test void successfulLoginDoesNotExpireWhenIdle() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        HttpSession session = mock(HttpSession.class);
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        when(user.getPassword()).thenReturn("encoded");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("password", "encoded")).thenReturn(true);
        UserServiceImpl service = new UserServiceImpl();
        ReflectionTestUtils.setField(service, "userRepository", repository);
        ReflectionTestUtils.setField(service, "passwordEncoder", encoder);
        service.login("test@example.com", "password", session);
        verify(session).setMaxInactiveInterval(-1);
        verify(session).setAttribute("USER_ID", 7L);
    }

    @Test void invalidPasswordDoesNotCreatePersistentLogin() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        HttpSession session = mock(HttpSession.class);
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("encoded");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        UserServiceImpl service = new UserServiceImpl();
        ReflectionTestUtils.setField(service, "userRepository", repository);
        ReflectionTestUtils.setField(service, "passwordEncoder", encoder);
        assertThrows(IllegalStateException.class, () -> service.login("test@example.com", "wrong", session));
        verifyNoInteractions(session);
    }
}
