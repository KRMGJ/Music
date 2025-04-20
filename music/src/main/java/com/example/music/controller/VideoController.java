package com.example.music.controller;

import com.example.music.model.Video;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                         Model model) {
        List<Video> videos = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            videos = youtubeService.searchVideos(query, channel, page);
        }

        if (filter.equals("shorts")) {
            videos = videos.stream().filter(Video::isShorts).collect(Collectors.toList());
        } else if (filter.equals("videos")) {
            videos = videos.stream().filter(v -> !v.isShorts()).collect(Collectors.toList());
        }

        model.addAttribute("videos", videos);
        model.addAttribute("query", query);
        model.addAttribute("channel", channel);
        model.addAttribute("currentPage", page);
        return "video/search";
    }

    @PostMapping("/addVideo")
    public String addVideo(@RequestBody Video video) {
        videoService.insertVideo(video);
        return "Video added successfully";
    }
}
