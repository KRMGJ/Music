package com.example.music.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.music.handler.GoogleAccessTokenResolver;
import com.example.music.model.CommentPostRequest;
import com.example.music.model.Comments;
import com.example.music.model.PageResponse;
import com.example.music.model.Playlist;
import com.example.music.model.User;
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;
import com.example.music.model.YoutubePlaylist;
import com.example.music.service.PlaylistService;
import com.example.music.service.YoutubeService;
import com.example.music.service.serviceImpl.YoutubeServiceImpl.RelatedResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/youtube")
public class YoutubeController {

	@Autowired
	YoutubeService youtubeService;

	@Autowired
	PlaylistService playlistService;

	@Autowired
	GoogleAccessTokenResolver tokenResolver;

	@GetMapping("/{id}")
	public String detail(@PathVariable("id") String id, Model model, HttpSession session) {
		User user = (User) session.getAttribute("loginUser");

		VideoDetail video = youtubeService.getDetail(id);
		List<VideoListItem> related = youtubeService.getRelated(id, 12);
		RelatedResponse resp = youtubeService.getRelatedPage(id, null, 12);
		List<Playlist> playlists = playlistService.getPlaylistsWithLastThumbnail(user.getId());

		model.addAttribute("video", video);
		model.addAttribute("related", related);
		model.addAttribute("nextPageToken", resp.getNextPageToken());
		model.addAttribute("playlists", playlists);

		return "youtube/video";
	}

	@GetMapping("/{id}/related")
	@ResponseBody
	public RelatedResponse moreRelated(@PathVariable String id, @RequestParam(required = false) String pageToken,
			@RequestParam(defaultValue = "12") int size) {
		return youtubeService.getRelatedPage(id, pageToken, size);
	}

	@GetMapping("/{id}/comments")
	@ResponseBody
	public Comments.Page getComments(@PathVariable("id") String videoId,
			@RequestParam(value = "pageToken", required = false) String pageToken,
			@RequestParam(value = "order", required = false) String order, // "time" | "relevance"
			@RequestParam(value = "size", required = false) Integer pageSize // 1~100
	) {
		return youtubeService.getComments(videoId, pageToken, order, pageSize);
	}

	@PostMapping("/{videoId}/comments")
	public ResponseEntity<?> postComment(@PathVariable String videoId, @RequestBody CommentPostRequest req,
			HttpSession session) {
		try {
			if (req == null) {
				return ResponseEntity.badRequest().body(Map.of("message", "요청 바디가 없습니다."));
			}
			String text = req.getText() == null ? "" : req.getText().trim();
			if (text.isEmpty()) {
				return ResponseEntity.badRequest().body(Map.of("message", "댓글 내용을 입력해 주세요."));
			}

			req.setVideoId(videoId);

			String accessToken = tokenResolver.getValidAccessToken(session);
			if (accessToken == null) {
				return ResponseEntity.status(401).body("{\"message\":\"세션 만료 또는 권한 없음. 다시 로그인해 주세요.\"}");
			}

			Object result = youtubeService.postComment(req, accessToken);
			return ResponseEntity.ok(result);
		} catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
			var d = e.getDetails();
			int code = d != null ? d.getCode() : 400;
			String reason = (d != null && d.getErrors() != null && !d.getErrors().isEmpty())
					? d.getErrors().get(0).getReason()
					: null;
			String msg = (d != null && d.getMessage() != null) ? d.getMessage() : "요청 거부";
			return ResponseEntity.status(code).body(Map.of("message", "댓글 등록 실패: " + msg, "reason", reason));
		} catch (org.springframework.web.client.HttpClientErrorException e) {
			return ResponseEntity.status(e.getStatusCode())
					.body(Map.of("message", "댓글 등록 실패: " + e.getStatusText(), "body", e.getResponseBodyAsString()));
		} catch (Exception e) {
			// 무엇이든 원인 보이게
			return ResponseEntity.status(500).body(Map.of("message", "댓글 등록 실패: 서버 오류", "error",
					e.getClass().getSimpleName(), "detail", String.valueOf(e.getMessage())));
		}
	}

	@GetMapping("/my-playlists")
	public String myPlaylists(@RequestParam(defaultValue = "24") int size,
			@RequestParam(required = false) String pageToken, HttpSession session, Model model) {
		String accessToken = tokenResolver.getValidAccessToken(session);
		PageResponse<YoutubePlaylist> page = youtubeService.getMyPlaylists(accessToken, size, pageToken);

		model.addAttribute("items", page.getItems());
		model.addAttribute("nextPageToken", page.getNextPageToken());
		model.addAttribute("prevPageToken", page.getPrevPageToken());
		model.addAttribute("size", size);
		return "playlist/list";
	}
}
