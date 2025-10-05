package com.example.music.controller;

import static com.example.music.util.MessageUtil.errorMessage;
import static com.example.music.util.MessageUtil.errorMessageWithRedirect;
import static com.example.music.util.MessageUtil.successMessage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.music.model.User;
import com.example.music.service.UserService;

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
	public String userList(HttpSession session, Model model, HttpServletRequest request) {
		if (session.getAttribute("loginUser") == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
			return "common/error";
		}
		List<User> userList = userService.getAllUsers();
		model.addAttribute("userList", userList);
		return "admin/userList";
	}

	@GetMapping("/updateUser/{email}")
	public String updateUser(@PathVariable String email, HttpSession session, Model model, HttpServletRequest request) {
		Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
		if (session.getAttribute("loginUser") == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
			return "common/error";
		}
		if (!isAdmin) {
			errorMessage("관리자 권한이 필요합니다.", model);
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
			successMessage("회원 정보가 수정되었습니다.", "/admin/userList", model);
			return "common/success";
		} catch (Exception exception) {
			errorMessage("회원 정보 수정 중 오류가 발생했습니다.", "/admin/updateUser/{userId}", model);
			return "common/error";
		}
	}

	@PostMapping("/deleteUser/{email}")
	public String deleteUser(@PathVariable String email, HttpSession session, Model model, HttpServletRequest request) {
		Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
		if (session.getAttribute("loginUser") == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
			return "common/error";
		}
		if (!isAdmin) {
			errorMessage("관리자 권한이 필요합니다.", model);
			return "common/error";
		}
		try {
			userService.deleteUser(email);
			successMessage("회원 정보가 삭제되었습니다.", "/admin/userList", model);
			return "common/success";
		} catch (Exception exception) {
			errorMessage("회원 정보 삭제 중 오류가 발생했습니다.", "/admin/userList", model);
			return "common/error";
		}
	}

}
