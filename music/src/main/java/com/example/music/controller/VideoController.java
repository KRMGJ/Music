package com.example.music.controller;

import com.example.music.model.SearchList;
import com.example.music.model.Video;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/video")
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    YoutubeService youtubeService;

    @GetMapping("/")
    public String home() {
        return "video/home";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "query", required = false) String query,
                         @RequestParam(value = "channel", required = false) String channel,
                         @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                         @RequestParam(value = "filter", defaultValue = "all") String filter,
                         @RequestParam(value = "sort", defaultValue = "relevance") String sort,
                         Model model) {

        SearchList result = youtubeService.searchVideos(query, channel, page, filter, sort);

        model.addAttribute("searchResult", result);
        model.addAttribute("query", query);
        model.addAttribute("channel", channel);
        model.addAttribute("filter", filter);
        return "video/search";
    }

    @PostMapping("/addVideo")
    public String addVideo(@RequestBody Video video) {
        videoService.insertVideo(video);
        return "Video added successfully";
    }
}
