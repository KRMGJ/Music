package com.example.music.util;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class MessageUtil {
	
	public static void successMessage(String message, Model model) {
		model.addAttribute("message", message);
	}
	
	public static void successMessage(String message, String redirectUrl, Model model) {
		model.addAttribute("message", message);
		model.addAttribute("redirectUrl", redirectUrl);
	}
	
	public static void errorMessage(String message, Model model) {
		model.addAttribute("message", message);
	}
	
	public static void errorMessage(String message, String redirectUrl, Model model) {
		model.addAttribute("message", message);
		model.addAttribute("redirectUrl", redirectUrl);
	}
	
    public static void sessionErrorMessage(String message, String redirectUrl, HttpSession session) {
        session.setAttribute("errorMessage", message);
        session.setAttribute("redirectUrl", redirectUrl);
    }
}
