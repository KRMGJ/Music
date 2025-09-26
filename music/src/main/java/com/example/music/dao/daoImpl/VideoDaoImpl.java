package com.example.music.dao.daoImpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.music.dao.VideoDao;
import com.example.music.model.Video;

@Repository
public class VideoDaoImpl implements VideoDao {

	@Autowired
	SqlSession sqlSession;

	@Override
	public void insertVideo(Video video) {
		sqlSession.insert("video.addVideo", video);
	}

	@Override
	public void updateVideo(String videoId) {
		sqlSession.update("video.updateVideo", videoId);
	}

	@Override
	public void deleteVideo(String id) {
		sqlSession.delete("video.deleteVideo", id);
	}

	@Override
	public Video getVideoById(String videoId) {
		Video video = sqlSession.selectOne("video.getVideoById", videoId);
		return video;
	}

	@Override
	public List<Video> getAllVideos() {
		List<Video> videoList = sqlSession.selectList("video.getAllVideos");
		return videoList;
	}

	@Override
	public List<Video> getVideosByChannel(String channelTitle) {
		List<Video> videoList = sqlSession.selectList("video.getVideosByChannel", channelTitle);
		return videoList;
	}

	@Override
	public void insertPlaylistVideo(Long playlistId, String videoId) {
		sqlSession.insert("video.insertPlaylistVideo", new Object[] { playlistId, videoId });
	}

	@Override
	public List<Video> getVideosByVideoIds(List<String> videoIds) {
		List<Video> videos = sqlSession.selectList("video.getVideosByVideoIds", videoIds);
		return videos;
	}
}
