package com.example.music.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelSummary {
	private String channelId;
	private String channelTitle;
	private String channelThumbnail;
	private Long subscriberCount; // 비공개면 null/0
	private Long videoCount;
}
