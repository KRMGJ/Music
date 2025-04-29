package com.example.music.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.music.model.User;
import com.example.music.service.UserService;
import com.example.music.util.MessageUtil;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	UserService userService;

	@GetMapping("/forbidden")
	public String forbidden() {
		return "common/forbidden";
	}

	@GetMapping("/userList")
	public String userList(HttpSession session, Model model) {
		if (session.getAttribute("loginUser") == null) {
			MessageUtil.errorMessage("로그인이 필요합니다.", "/auth/login", model);
			return "common/error";
		}
		List<User> userList = userService.getAllUsers();
		model.addAttribute("userList", userList);
		return "admin/userList";
	}

	@GetMapping("/updateUser/{email}")
	public String updateUser(@PathVariable String email, HttpSession session, Model model) {
		Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
		if (session.getAttribute("loginUser") == null) {
			MessageUtil.errorMessage("로그인이 필요합니다.", "/auth/login", model);
			return "common/error";
		}
		if (!isAdmin) {
			MessageUtil.errorMessage("관리자 권한이 필요합니다.", model);
			return "common/error";
		}
		User user = userService.getUserByEmail(email);
		model.addAttribute("user", user);
		return "user/updateUser";
	}

	@PostMapping("/updateUser")
	public String updateUser(@ModelAttribute User user, Model model) {
		try {
			userService.updateUser(user);
			MessageUtil.successMessage("회원 정보가 수정되었습니다.", "/admin/userList", model);
			return "common/success";
		} catch (Exception exception) {
			MessageUtil.errorMessage("회원 정보 수정 중 오류가 발생했습니다.", "/admin/updateUser/{userId}", model);
			return "common/error";
		}
	}

	@PostMapping("/deleteUser/{email}")
	public String deleteUser(@PathVariable String email, HttpSession session, Model model) {
		Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
		if (session.getAttribute("loginUser") == null) {
			MessageUtil.errorMessage("로그인이 필요합니다.", "/auth/login", model);
			return "common/error";
		}
		if (!isAdmin) {
			MessageUtil.errorMessage("관리자 권한이 필요합니다.", model);
			return "common/error";
		}
		try {
			userService.deleteUser(email);
			MessageUtil.successMessage("회원 정보가 삭제되었습니다.", "/admin/userList", model);
			return "common/success";
		} catch (Exception exception) {
			MessageUtil.errorMessage("회원 정보 삭제 중 오류가 발생했습니다.", "/admin/userList", model);
			return "common/error";
		}
	}

}
