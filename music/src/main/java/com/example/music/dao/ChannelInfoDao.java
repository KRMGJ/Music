package com.example.music.dao;

import com.example.music.model.ChannelInfo;

public interface ChannelInfoDao {
    void insertChannelInfo(ChannelInfo channelInfo);
    void updateChannelInfo(ChannelInfo channelInfo);
    ChannelInfo getChannelInfoById(String channelId);
}
