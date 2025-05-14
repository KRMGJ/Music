package com.example.music.controller.restController;

import com.example.music.model.Playlist;
import com.example.music.model.User;
import com.example.music.model.Video;
import com.example.music.service.PlaylistService;
import com.example.music.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/playlist")
public class PlaylistRestController {

    @Autowired
    YoutubeService youtubeService;

    @Autowired
    PlaylistService playlistService;

    @PostMapping("/addVideo")
    public ResponseEntity<?> addVideoToPlaylist(@RequestParam String playlistId,
                                                @RequestParam String videoId) {
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
    public ResponseEntity<?> createPlaylist(@ModelAttribute Playlist playlist,
                                 HttpSession session) {
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
