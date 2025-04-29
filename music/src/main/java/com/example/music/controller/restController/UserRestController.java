package com.example.music.controller.restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.music.model.User;
import com.example.music.service.UserService;

@RestController
@RequestMapping("/user")
public class UserRestController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/signUp")
	public ResponseEntity<?> addUser(@ModelAttribute User user, Model model) {
		try {
			userService.addUser(user);
			return ResponseEntity.ok("회원가입 성공");
		} catch (Exception exception) {
			return ResponseEntity.status(401).body("회원가입 실패");
		}
	}
}
