package com.vsoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vsoft.entity.User;
import com.vsoft.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String register(String fullName, String email, String phone, String degree, String password) {
        if (repo.findByEmail(email).isPresent()) {
            return "Username already exists";
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setDegree(degree);
        user.setPhone(phone);
        user.setPassword(encoder.encode(password)); // store encrypted
        
        repo.save(user);
        return "User registered successfully";
    }

    public boolean login(String email, String password) {
        return repo.findByEmail(email)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }
}
