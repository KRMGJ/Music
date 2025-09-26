package com.example.music.controller.restController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.music.model.VideoSummary;
import com.example.music.service.HomeFeedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeRestController {

	private final HomeFeedService homeFeedService;

	@GetMapping("/latest")
	public List<VideoSummary> latest(@RequestParam(defaultValue = "KR") String region,
			@RequestParam(defaultValue = "12") int limit) throws Exception {
		return homeFeedService.getLatestForHome(region, limit);
	}

}
