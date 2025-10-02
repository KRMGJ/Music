package com.example.music.service.serviceImpl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.music.dao.PlaylistDao;
import com.example.music.dao.PlaylistVideoDao;
import com.example.music.dao.VideoDao;
import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;
import com.example.music.model.Video;
import com.example.music.service.PlaylistService;
import com.example.music.service.VideoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlaylistServiceImpl implements PlaylistService {

	@Autowired
	PlaylistDao playListDao;

	@Autowired
	VideoDao videoDao;

	@Autowired
	PlaylistVideoDao playListVideoDao;

	@Autowired
	VideoService videoService;

	@Override
	public String addVideoToPlayList(Video video, String playlistId) {
		// 1. videos 테이블에 없으면 저장
		Video existing = videoDao.getVideoById(video.getVideoId());
		if (existing == null) {
			videoService.insertVideo(video);
		}
		// 2. playlist_videos 테이블에 매핑 추가
		List<String> videoIds = playListVideoDao.getVideosByPlaylistId(playlistId);
		if (videoIds != null && videoIds.contains(video.getVideoId())) {
			return "error";
		}
		playListVideoDao.insert(playlistId, video.getVideoId());
		return "success";
	}

	@Override
	public void addPlayList(Playlist playList) {
		playListDao.insert(playList);
	}

	@Override
	public void updatePlayList(Playlist playList) {
		playListDao.update(playList);
	}

	@Override
	public void deletePlaylistByPlaylistId(String id) {
		playListDao.delete(id);
	}

	@Override
	public void deleteVideoFromPlayList(PlaylistVideo pv) {
		playListVideoDao.deleteVideoFromPlaylist(pv);
	}

	@Override
	public List<Video> getVideosByPlaylistId(String playlistId) {
		List<String> videoIds = playListVideoDao.getVideosByPlaylistId(playlistId);
		if (videoIds != null && !videoIds.isEmpty()) {
			List<Video> videos = videoDao.getVideosByVideoIds(videoIds);
			return videos;
		}
		return Collections.emptyList();
	}

	@Override
	public Playlist getPlaylistByPlaylistId(String playlistId) {
		Playlist playList = playListDao.getPlaylistByPlaylistId(playlistId);
		return playList;
	}

	@Override
	public List<Playlist> getPlaylistsWithLastThumbnail(String userId) {
		List<Playlist> playList = playListDao.getPlaylistsWithLastThumbnailByUserId(userId);
		return playList;
	}

	@Override
	public List<Playlist> getPlayListByTitle(String title) {
		List<Playlist> playList = playListDao.getPlaylistsByTitle(title);
		return playList;
	}

	@Override
	public List<Playlist> getAllPlaylists() {
		List<Playlist> playList = playListDao.getAllPlaylists();
		return playList;
	}

	@Override
	public List<Playlist> getPlaylistsByUserId(String userId) {
		List<Playlist> playList = playListDao.getPlaylistsByUserId(userId);
		return playList;
	}
}
