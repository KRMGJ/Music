package com.example.music.controller;

import com.example.music.model.User;
import com.example.music.model.Video;
import com.example.music.service.PlayListService;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import com.example.music.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/playlist")
public class PlayListController {

    @Autowired
    YoutubeService youtubeService;

    @Autowired
    PlayListService playlistService;

    @PostMapping("/addVideo")
    public String addVideoToPlaylist(@RequestParam("playlistId") int playlistId,
                                     @RequestParam("videoId") String videoId,
                                     Model model) {
        Video video = youtubeService.fetchAndSaveVideoById(videoId);

        playlistService.addVideoToPlayList(video, playlistId); // 매핑 테이블에 추가
        MessageUtil.successMessage("재생목록에 추가되었습니다.", model);
        return "common/success";
    }
}
