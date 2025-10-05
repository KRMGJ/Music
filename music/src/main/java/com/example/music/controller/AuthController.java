package com.example.music.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.music.model.User;
import com.example.music.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	UserService userService;

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@PostMapping("/signUp")
	@ResponseBody
	public ResponseEntity<?> addUser(@ModelAttribute User user) {
		try {
			userService.addUser(user);
			return ResponseEntity.ok("회원가입 성공");
		} catch (Exception exception) {
			exception.printStackTrace();
			return ResponseEntity.status(401).body("회원가입 실패");
		}
	}

	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpSession session) {
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return ResponseEntity.status(401).body("User not found");
		}
		if (!user.getPassword().equals(password)) {
			return ResponseEntity.status(401).body("Invalid password");
		}
		session.setAttribute("loginUser", user);
		session.setAttribute("isAdmin", user.getRole().equals("ADMIN"));
		return ResponseEntity.ok("Login successful");
	}

	@PostMapping("/logout")
	@ResponseBody
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("Logout successful");
	}
}
