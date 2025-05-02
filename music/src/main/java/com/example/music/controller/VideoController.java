package com.example.music.controller;

import com.example.music.model.Playlist;
import com.example.music.model.SearchList;
import com.example.music.model.User;
import com.example.music.service.PlaylistService;
import com.example.music.service.YoutubeService;
import com.example.music.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

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
                         @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                         HttpSession session, Model model) {
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
}
