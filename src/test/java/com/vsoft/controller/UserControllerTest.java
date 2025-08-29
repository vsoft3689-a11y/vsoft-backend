package com.vsoft.controller;

import com.vsoft.entity.User;
import com.vsoft.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.register(any(User.class))).thenReturn(Map.of("status", "success"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Demo User\",\"email\":\"demo1@gmail.com\",\"password\":\"12345\",\"phone\":\"9876543210\",\"degree\":\"B.Tech\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testLogin_Success() throws Exception {
        User user = new User();
        user.setFullName("Demo User");
        user.setEmail("demo1@gmail.com");
        user.setPassword("12345");

        when(userService.login("demo1@gmail.com", "12345")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"demo1@gmail.com\",\"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.user").value("Demo User"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        when(userService.login("demo1@gmail.com", "wrongpass")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"demo1@gmail.com\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void testLogin_EmailMissing() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Email is required"));
    }

    @Test
    void testLogin_InvalidEmailFormat() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalidEmail\",\"password\":\"12345\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Invalid email format"));
    }

    @Test
    void testLogin_PasswordMissing() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"demo1@gmail.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Password is required"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}