package com.example.music.dao.daoImpl;

import com.example.music.model.Video;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.music.dao.VideoDao;

import java.util.List;

@Repository
public class VideoDaoImpl implements VideoDao {

    @Autowired
    SqlSession sqlSession;

    @Override
    public void insertVideo(Video video) {
        sqlSession.insert("Video.addVideo", video);
    }

    @Override
    public void updateVideo(Video video) {
        sqlSession.update("Video.updateVideo", video);
    }

    @Override
    public void deleteVideo(String id) {
        sqlSession.delete("Video.deleteVideo", id);
    }

    @Override
    public Video getVideoById(String id) {
        Video video = sqlSession.selectOne("Video.getVideoById", id);
        return video;
    }

    @Override
    public List<Video> getAllVideos() {
        List<Video> videoList = sqlSession.selectList("Video.getAllVideos");
        return videoList;
    }

    @Override
    public List<Video> getVideosByChannel(String channelTitle) {
        List<Video> videoList = sqlSession.selectList("Video.getVideosByChannel", channelTitle);
        return videoList;
    }
}
