package com.example.music.controller;

import static com.example.music.util.MessageUtil.errorMessageWithRedirect;
import static com.example.music.util.MessageUtil.successMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.music.model.User;
import com.example.music.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/myPage")
	public String myPage(HttpSession session, Model model, HttpServletRequest request) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
			return "common/error";
		}
		String email = loginUser.getEmail();
		User user = userService.getUserByEmail(email);
		model.addAttribute("user", user);
		return "user/myPage";
	}

	@GetMapping("/updateUser")
	public String updateUser(HttpSession session, Model model, HttpServletRequest request) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
			return "common/error";
		}
		String email = loginUser.getEmail();
		User user = userService.getUserByEmail(email);
		model.addAttribute("user", user);
		return "user/updateUser";
	}

	@PostMapping("/updateUser")
	public String updateUser(@ModelAttribute User user) {
		userService.updateUser(user);
		return "redirect:/user/myPage?email=" + user.getEmail();
	}

	@PostMapping("/deleteUser")
	public String deleteUser(HttpSession session, Model model, HttpServletRequest request) {
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
			return "common/error";
		}
		String email = loginUser.getEmail();
		userService.deleteUser(email);
		session.invalidate();
		successMessage("회원탈퇴가 완료되었습니다.", "/", model);
		return "common/success";
	}
}
