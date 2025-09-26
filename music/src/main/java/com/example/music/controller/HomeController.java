package com.example.music.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.music.service.HomeFeedService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final HomeFeedService homeFeedService;

	@GetMapping("/")
	public String home(Model model) throws Exception {
		model.addAttribute("popularVideos", homeFeedService.getPopularForHome("KR", 12));
		return "home";
	}

}
