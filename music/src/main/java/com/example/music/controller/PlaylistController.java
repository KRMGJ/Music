package com.example.music.controller;

import com.example.music.model.*;
import com.example.music.service.PlaylistService;
import com.example.music.service.YoutubeService;
import com.example.music.util.MessageUtil;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    YoutubeService youtubeService;

    @Autowired
    PlaylistService playlistService;

    @GetMapping("/list")
    public String getPlaylists(HttpSession session, Model model) {
        // 1. 로그인한 사용자 정보 가져오기
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            MessageUtil.errorMessage("로그인 후 이용해주세요.", "/auth/login", model);
            return "common/error";
        }
        // 2. 사용자의 재생목록 가져오기
        List<Playlist> playlists = playlistService.getPlaylistsByUserId(user.getId());
        model.addAttribute("playlists", playlists);
        return "playlist/list";
    }

    @GetMapping("/create")
    public String createPlaylistForm(HttpSession session, Model model) {
        // 1. 로그인한 사용자 정보 가져오기
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            MessageUtil.errorMessage("로그인 후 이용해주세요.", "/auth/login", model);
            return "common/error";
        }
        // 2. 재생목록 생성 폼으로 이동
        return "playlist/create";
    }

    @PostMapping("/create")
    public String createPlaylist(@ModelAttribute Playlist playlist,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // 1. 로그인한 사용자 정보 가져오기
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            MessageUtil.errorMessage("로그인 후 이용해주세요.", "/auth/login", redirectAttributes);
            return "common/error";
        }
        // 2. 재생목록 생성
        playlist.setUserId(user.getId());
        playlistService.addPlayList(playlist);
        MessageUtil.successMessage("재생목록이 생성되었습니다.", redirectAttributes);
        return "common/success";
    };

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
            MessageUtil.errorMessage("이미 추가된 비디오입니다.", redirectAttributes);
        } else {
            MessageUtil.successMessage("비디오가 재생목록에 추가되었습니다.", redirectAttributes);
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
        // 2. 재생목록 정보 가져오기
        Playlist playlist = playlistService.getPlaylistByPlaylistId(playlistId);
        model.addAttribute("playlistVideos", playlistVideos );
        model.addAttribute("playlist", playlist);
        return "playlist/videos";
    }

    @PostMapping("/remove")
    public String removeVideoFromPlaylist(@RequestParam("playlistId") int playlistId,
                                          @RequestParam("videoId") String videoId,
                                          RedirectAttributes redirectAttributes) {
        // 비디오 삭제
        PlaylistVideo video = new PlaylistVideo(playlistId, videoId);
        playlistService.deleteVideoFromPlayList(video);
        MessageUtil.successMessage("비디오가 재생목록에서 삭제되었습니다.", redirectAttributes);
        return "redirect:/playlist/videos?playlistId=" + playlistId;
    }
}
