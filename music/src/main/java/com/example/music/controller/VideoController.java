package com.example.music.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.music.model.Comments;
import com.example.music.model.Playlist;
import com.example.music.model.SearchList;
import com.example.music.model.User;
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;
import com.example.music.service.PlaylistService;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import com.example.music.service.serviceImpl.VideoServiceImpl.RelatedResponse;
import com.example.music.util.MessageUtil;

@Controller
@RequestMapping("/video")
public class VideoController {

	@Autowired
	PlaylistService playListService;

	@Autowired
	YoutubeService youtubeService;

	@Autowired
	VideoService videoService;

	@GetMapping("/")
	public String home() {
		return "video/home";
	}

	@GetMapping("/search")
	public String search(@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "sort", defaultValue = "relevance") String sort,
			@RequestParam(value = "duration", required = false) String duration,
			@RequestParam(value = "upload", required = false) String upload,
			@RequestParam(value = "regionCode", required = false) String regionCode,
			@RequestHeader(value = "X-Requested-With", required = false) String requestedWith, HttpSession session,
			Model model) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			MessageUtil.errorMessage("로그인 후 이용해주세요.", "/auth/login", model);
			return "common/error";
		}

		List<Playlist> playlists = playListService.getPlaylistsByUserId(user.getId());
		SearchList result = youtubeService.searchVideos(query, page, sort, duration, upload, regionCode);

		model.addAttribute("playlists", playlists);
		model.addAttribute("searchResult", result);
		model.addAttribute("query", query);
		model.addAttribute("sort", sort);
		model.addAttribute("duration", duration);
		model.addAttribute("upload", upload);
		model.addAttribute("regionCode", regionCode);

		if ("XMLHttpRequest".equals(requestedWith)) {
			return "video/searchResultsFragment"; // .jsp 생략
		}

		return "video/search";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable("id") String id, Model model) {
		VideoDetail video = videoService.getDetail(id);
		List<VideoListItem> related = videoService.getRelated(id, 12);
		RelatedResponse resp = videoService.getRelatedPage(id, null, 12);

		model.addAttribute("video", video);
		model.addAttribute("related", related);
		model.addAttribute("nextPageToken", resp.getNextPageToken());

		return "video/video";
	}

	@GetMapping("/{id}/related")
	@ResponseBody
	public RelatedResponse moreRelated(@PathVariable String id, @RequestParam(required = false) String pageToken,
			@RequestParam(defaultValue = "12") int size) {
		return videoService.getRelatedPage(id, pageToken, size);
	}

	@GetMapping("/{id}/comments")
	@ResponseBody
	public Comments.Page getComments(@PathVariable("id") String videoId,
			@RequestParam(value = "pageToken", required = false) String pageToken,
			@RequestParam(value = "order", required = false) String order, // "time" | "relevance"
			@RequestParam(value = "size", required = false) Integer pageSize // 1~100
	) {
		return videoService.getComments(videoId, pageToken, order, pageSize);
	}
}
