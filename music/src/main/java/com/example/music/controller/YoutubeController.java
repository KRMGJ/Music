package com.example.music.controller;

import java.util.List;

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
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;
import com.example.music.service.YoutubeService;
import com.example.music.service.serviceImpl.YoutubeServiceImpl.RelatedResponse;

@Controller
@RequestMapping("/youtube")
public class YoutubeController {

	@Autowired
	YoutubeService youtubeService;

	@Autowired
	GoogleAccessTokenResolver tokenResolver;

	@GetMapping("/{id}")
	public String detail(@PathVariable("id") String id, Model model) {
		VideoDetail video = youtubeService.getDetail(id);
		List<VideoListItem> related = youtubeService.getRelated(id, 12);
		RelatedResponse resp = youtubeService.getRelatedPage(id, null, 12);

		model.addAttribute("video", video);
		model.addAttribute("related", related);
		model.addAttribute("nextPageToken", resp.getNextPageToken());

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
			req.setVideoId(videoId);

			String accessToken = tokenResolver.getValidAccessToken(session);
			if (accessToken == null) {
				return ResponseEntity.status(401).body("{\"message\":\"세션 만료 또는 권한 없음. 다시 로그인해 주세요.\"}");
			}

			Object result = youtubeService.postComment(req, accessToken);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			String msg = e.getMessage();
			return ResponseEntity.status(400).body("{\"message\":\"댓글 등록 실패: " + msg + "\"}");
		}
	}
}
