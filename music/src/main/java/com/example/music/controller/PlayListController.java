package com.example.music.controller;

import com.example.music.model.PlayList;
import com.example.music.model.SearchList;
import com.example.music.model.User;
import com.example.music.model.Video;
import com.example.music.service.PlayListService;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import com.example.music.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

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
                                     @RequestParam(value = "query") String query,
                                     @RequestParam(value = "channel", required = false) String channel,
                                     @RequestParam(value = "filter", defaultValue = "all") String filter,
                                     @RequestParam(value = "sort", defaultValue = "relevance") String sort,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     RedirectAttributes redirectAttributes) {
        // 비디오 저장
        Video video = youtubeService.fetchAndSaveVideoById(videoId);
        String message = playlistService.addVideoToPlayList(video, playlistId);

        if (message.equals("error")) {
            redirectAttributes.addFlashAttribute("message", "이미 추가된 비디오입니다.");
        } else {
            redirectAttributes.addFlashAttribute("message", "비디오가 재생목록에 추가되었습니다.");
        }

        // 리다이렉트 시 파라미터 유지
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("channel", channel);
        redirectAttributes.addAttribute("filter", filter);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("page", page);

        return "redirect:/video/search";
    }


    @GetMapping("/videos")
    public String getVideosByPlaylistId(@RequestParam("playlistId") int playlistId,
                                        Model model) {
        // 1. 재생목록에 포함된 비디오 목록 가져오기
        List<Video> playlistVideos  = playlistService.getVideosByPlaylistId(playlistId);
        model.addAttribute("playlistVideos", playlistVideos );
        return "playlist/videos";
    }
}
