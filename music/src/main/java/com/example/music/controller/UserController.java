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
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/addUser")
	public String addUser() {
		return "user/addUser";
	}

	@PostMapping("/addUser")
	public String addUser(@ModelAttribute User user, Model model) {
		try {
			userService.addUser(user);
			MessageUtil.successMessage("회원가입이 완료되었습니다.", "/auth/login", model);
			return "common/success";
		} catch (Exception exception) {
			MessageUtil.errorMessage("회원가입 중 오류가 발생했습니다.", "/user/addUser", model);
			return "common/error";
		}
	}

	@GetMapping("/myPage")
	public String myPage(HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			MessageUtil.errorMessage("로그인이 필요합니다.", "/auth/login", model);
			return "common/error";
		}
		String userId = loginUser.getUserId();
		User user = userService.getUserByUserId(userId);
		model.addAttribute("user", user);
		return "user/myPage";
	}

	@GetMapping("/updateUser")
	public String updateUser(HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			MessageUtil.errorMessage("로그인이 필요합니다.", "/auth/login", model);
			return "common/error";
		}
		String userId = loginUser.getUserId();
		User user = userService.getUserByUserId(userId);
		model.addAttribute("user", user);
		return "user/updateUser";
	}

	@PostMapping("/updateUser")
	public String updateUser(@ModelAttribute User user) {
		userService.updateUser(user);
		return "redirect:/user/myPage?userId=" + user.getUserId();
	}

	@PostMapping("/deleteUser")
	public String deleteUser(HttpSession session, Model model) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			MessageUtil.errorMessage("로그인이 필요합니다.", "/auth/login", model);
			return "common/error";
		}
		String userId = loginUser.getUserId();
		userService.deleteUser(userId);
		session.invalidate();
		MessageUtil.successMessage("회원탈퇴가 완료되었습니다.", "/", model);
		return "common/success";
	}
}
