package com.vsoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vsoft.service.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public String register(@RequestParam String fullName, @RequestParam String email, @RequestParam String phone,
			@RequestParam String degree, @RequestParam String password) {
		return userService.register(fullName, email, phone, degree, password);
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password) {
		boolean success = userService.login(email, password);
		return success ? "Login successful" : "Invalid credentials";
	}
}
