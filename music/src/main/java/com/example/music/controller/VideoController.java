package com.example.music.controller;

import com.example.music.model.SearchList;
import com.example.music.model.Video;
import com.example.music.service.VideoService;
import com.example.music.service.YoutubeService;
import com.example.music.service.serviceImpl.YoutubeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                         @RequestParam(value = "sort", defaultValue = "relevance") String sort,
                         Model model) {

        SearchList searchResult = youtubeService.searchVideos(query, channel, 1, sort);
        List<Video> filtered = searchResult.getVideos();

        // 필터링
        if (filter.equals("shorts")) {
            filtered = filtered.stream().filter(Video::isShorts).collect(Collectors.toList());
        } else if (filter.equals("videos")) {
            filtered = filtered.stream().filter(v -> !v.isShorts()).collect(Collectors.toList());
        }

        int pageSize = 10;
        int totalCount = filtered.size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        List<Video> paged = youtubeService.paginate(filtered, page, pageSize);

        SearchList result = new SearchList(paged, totalCount, totalPages, page, sort);
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
