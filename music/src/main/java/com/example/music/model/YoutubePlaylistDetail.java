package com.example.music.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YoutubePlaylistDetail {
	private String id;
	private String title;
	private String description;
	private String ownerChannelTitle;
	private String ownerChannelThumbnail;
	private String publishedAt;
	private Integer itemCount;
	private String coverImageUrl;
	private List<YoutubeVideoListItem> items;

	private String nextPageToken;
	private String prevPageToken;
}
