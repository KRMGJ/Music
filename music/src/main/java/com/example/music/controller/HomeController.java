package com.example.music.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.music.model.ChannelSummary;
import com.example.music.model.VideoSummary;
import com.example.music.service.HomeFeedService;

@Controller
public class HomeController {

	@Autowired
	HomeFeedService homeFeedService;

	@GetMapping("/")
	public String home(Model model) throws Exception {
		model.addAttribute("popularVideos", homeFeedService.getPopularForHome("KR", 12));
		return "home";
	}

	@GetMapping("/latest")
	@ResponseBody
	public List<VideoSummary> latest(@RequestParam(defaultValue = "KR") String region,
			@RequestParam(defaultValue = "12") int limit) throws Exception {
		return homeFeedService.getLatestForHome(region, limit);
	}

	@GetMapping("/channels")
	@ResponseBody
	public List<ChannelSummary> channels(@RequestParam(defaultValue = "KR") String region,
			@RequestParam(defaultValue = "16") int limit) throws Exception {
		return homeFeedService.getPopularChannels(region, limit);
	}

	@GetMapping("/popular")
	@ResponseBody
	public List<VideoSummary> popular(@RequestParam(defaultValue = "KR") String region,
			@RequestParam(defaultValue = "12") int limit) throws Exception {
		return homeFeedService.getPopularForHome(region, limit);
	}

}
