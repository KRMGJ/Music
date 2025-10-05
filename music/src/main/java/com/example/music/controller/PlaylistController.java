package com.example.music.controller;

import static com.example.music.util.MessageUtil.errorMessageWithRedirect;
import static com.example.music.util.MessageUtil.successMessage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.User;
import com.example.music.model.Video;
import com.example.music.service.PlaylistService;
import com.example.music.service.YoutubeService;

@Controller
@RequestMapping("/playlist")
public class PlaylistController {

	@Autowired
	YoutubeService youtubeService;

	@Autowired
	PlaylistService playlistService;

	@GetMapping("/list")
	public String getPlaylists(HttpSession session, Model model, HttpServletRequest request) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			errorMessageWithRedirect(request, "로그인 후 이용해주세요.", model);
			return "common/error";
		}
		List<Playlist> playlists = playlistService.getPlaylistsWithLastThumbnail(user.getId());
		model.addAttribute("playlists", playlists);
		return "playlist/list";
	}

	@GetMapping("/create")
	public String createPlaylistForm(HttpSession session, Model model, HttpServletRequest request) {
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			errorMessageWithRedirect(request, "로그인 후 이용해주세요.", model);
			return "common/error";
		}
		return "playlist/create";
	}

	@GetMapping("/videos")
	public String getVideosByPlaylistId(@RequestParam("playlistId") String playlistId, Model model) {
		List<Video> playlistVideos = playlistService.getVideosByPlaylistId(playlistId);
		Playlist playlist = playlistService.getPlaylistByPlaylistId(playlistId);
		model.addAttribute("playlistVideos", playlistVideos);
		model.addAttribute("playlist", playlist);
		return "playlist/videos";
	}

	@PostMapping("/remove")
	public String removeVideoFromPlaylist(@RequestParam("playlistId") String playlistId,
			@RequestParam("videoId") String videoId, RedirectAttributes redirectAttributes) {
		// 비디오 삭제
		PlaylistVideo video = new PlaylistVideo(playlistId, videoId);
		playlistService.deleteVideoFromPlayList(video);
		successMessage("비디오가 재생목록에서 삭제되었습니다.", redirectAttributes);
		return "redirect:/playlist/videos?playlistId=" + playlistId;
	}

	@PostMapping("/addVideo")
	@ResponseBody
	public ResponseEntity<?> addVideoToPlaylist(@RequestParam String playlistId, @RequestParam String videoId) {
		// 비디오 저장
		Video video = youtubeService.fetchAndSaveVideoById(videoId);
		String message = playlistService.addVideoToPlayList(video, playlistId);

		if (message.equals("error")) {
			return ResponseEntity.status(400).body("Error adding video to playlist");
		} else {
			return ResponseEntity.ok("Video added to playlist successfully");
		}
	}

	@PostMapping("/create")
	@ResponseBody
	public ResponseEntity<?> createPlaylist(@ModelAttribute Playlist playlist, HttpSession session) {
		// 1. 로그인한 사용자 정보 가져오기
		User user = (User) session.getAttribute("loginUser");
		if (user == null) {
			return ResponseEntity.status(401).body("로그인 후 이용해주세요.");
		}
		// 2. 재생목록 생성
		playlist.setUserId(user.getId());
		playlistService.addPlayList(playlist);
		return ResponseEntity.ok("플레이 리스트가 생성되었습니다.");
	};
}
