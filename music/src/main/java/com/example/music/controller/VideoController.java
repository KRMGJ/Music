package com.example.music.controller;

import static com.example.music.util.MessageUtil.errorMessageWithRedirect;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.music.model.Playlist;
import com.example.music.model.SearchList;
import com.example.music.model.User;
import com.example.music.service.PlaylistService;
import com.example.music.service.YoutubeService;

@Controller
@RequestMapping("/video")
public class VideoController {

	@Autowired
	PlaylistService playListService;

	@Autowired
	YoutubeService youtubeService;

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
			HttpServletRequest request, Model model) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			errorMessageWithRedirect(request, "로그인이 필요합니다.", model);
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
}
