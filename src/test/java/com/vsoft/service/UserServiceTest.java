package com.vsoft.service;
import com.vsoft.entity.User;
import com.vsoft.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private UserService userService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void testRegister_NewUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(repo.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> response = userService.register(user);

        assertEquals("User registered successfully", response.get("message"));
        assertEquals(201, response.get("status"));
        verify(repo, times(1)).findByEmail("test@example.com");
        verify(repo, times(1)).save(any(User.class));

        // Ensure password got encrypted
        assertTrue(encoder.matches("password123", user.getPassword()));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("password123");

        when(repo.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(user));

        Map<String, Object> response = userService.register(user);

        assertEquals("Email already exists", response.get("message"));
        assertEquals(409, response.get("status"));
        verify(repo, times(1)).findByEmail("existing@example.com");
        verify(repo, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword(encoder.encode("password123"));

        when(repo.findByEmail("login@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("login@example.com", "password123");

        assertTrue(result.isPresent());
        assertEquals("login@example.com", result.get().getEmail());
        verify(repo, times(1)).findByEmail("login@example.com");
    }

    @Test
    void testLogin_UserNotFound() {
        when(repo.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.login("notfound@example.com", "password123");

        assertTrue(result.isEmpty());
        verify(repo, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    void testLogin_PasswordMismatch() {
        User user = new User();
        user.setEmail("wrongpass@example.com");
        user.setPassword(encoder.encode("correctpassword"));

        when(repo.findByEmail("wrongpass@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("wrongpass@example.com", "wrongpassword");

        assertTrue(result.isEmpty());
        verify(repo, times(1)).findByEmail("wrongpass@example.com");
    }
}

