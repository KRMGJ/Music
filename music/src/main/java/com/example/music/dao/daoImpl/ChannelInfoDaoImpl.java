package com.example.music.dao.daoImpl;

import com.example.music.dao.ChannelInfoDao;
import com.example.music.model.ChannelInfo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelInfoDaoImpl implements ChannelInfoDao {

    @Autowired
    SqlSession sqlSession;

    @Override
    public void insertChannelInfo(ChannelInfo channelInfo) {
        sqlSession.insert("channelInfo.addChannelInfo",  channelInfo);
    }

    @Override
    public void updateChannelInfo(ChannelInfo channelInfo) {
        sqlSession.update("channelInfo.updateChannelInfo", channelInfo);
    }

    @Override
    public ChannelInfo getChannelInfoById(String channelId) {
        ChannelInfo channelInfo = sqlSession.selectOne("channelInfo.getChannelInfoById", channelId);
        return channelInfo;
    }
}
