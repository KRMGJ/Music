package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchList {
    private List<Video> videos;
    private int totalCount;
    private int totalPages;
    private int currentPage;
    private String sort;
}
