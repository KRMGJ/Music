package com.example.music.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfo {
    private String channelId;
    private String channelTitle;
    private String channelThumbnail;
    private long subscriberCount;
}
