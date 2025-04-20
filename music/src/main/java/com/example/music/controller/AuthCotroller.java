package com.example.music.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.music.model.User;
import com.example.music.service.UserService;
import com.example.music.util.MessageUtil;

@Controller
@RequestMapping("/auth")
public class AuthCotroller {

	@Autowired
	UserService userService;

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String userId, @RequestParam String password, HttpSession session, Model model) {
		User user = userService.getUserByUserId(userId);
		if (user != null && user.getPassword().equals(password)) {
			session.setAttribute("loginUser", user);
			session.setAttribute("isAdmin", user.getRole().equals("ADMIN"));
			Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
			System.out.println("isAdmin: " + isAdmin);
			MessageUtil.successMessage("로그인 성공", "/", model);
			return "common/success";
		}
		MessageUtil.errorMessage("로그인 실패", "/auth/login", model);
		return "common/error";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		MessageUtil.successMessage("로그아웃 성공", "/", null);
		return "common/success";
	}
}
