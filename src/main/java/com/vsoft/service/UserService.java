package com.vsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vsoft.entity.User;
import com.vsoft.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Map<String,Object> register(User user) {
        String email = user.getEmail();
        String password = user.getPassword();

        Map<String,Object> response = new HashMap<>();

        if (repo.findByEmail(email).isPresent()) {
            response.put("message", "Email already exists");
            response.put("status", 409);
            return response;
        }
        user.setPassword(encoder.encode(password)); // store encrypted password

        repo.save(user);
        response.put("message", "User registered successfully");
        response.put("status", 201);
        return response;
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = repo.findByEmail(email);
        if (user.isEmpty()) {
            return Optional.empty(); // user not found
        }

        if (encoder.matches(password, user.get().getPassword())) {
            return user; // success
        } else {
            return Optional.empty(); // password mismatch
        }
    }
}
