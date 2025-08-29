package com.vsoft.controller;

import com.vsoft.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vsoft.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {"https://sathvikagundapu.github.io", "http://127.0.0.1:5500", "http://localhost:5500"}, allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user, HttpSession session) {
        String email = user.getEmail();
        String password = user.getPassword();
        Map<String, Object> response = new HashMap<>();

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            response.put("error", "Email is required");
            return response;
//			return ResponseEntity.badRequest().body(response);
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            response.put("error", "Invalid email format");
            return response;
//			return ResponseEntity.badRequest().body(response);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("error", "Password is required");
            return response;
//			return ResponseEntity.badRequest().body(response);
        }
        Optional<User> userinfo = userService.login(email, password);

        if (userinfo.isPresent()) {
            session.setAttribute("user", userinfo.get());
            response.put("status", 200);
            response.put("message", "Login successful");
            response.put("sessionId", session.getId());
            response.put("user", userinfo.get().getFullName());
        } else {
            response.put("status", 401);
            response.put("message", "Invalid credentials");
        }
        return response;
    }

    // Logout
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        System.out.println(session);
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logged out successfully");
        return response;
    }
}