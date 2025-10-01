package com.example.music.dao.daoImpl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.music.dao.PlaylistDao;
import com.example.music.model.Playlist;

@Repository
public class PlaylistDaoImpl implements PlaylistDao {

	@Autowired
	SqlSession sqlSession;

	@Override
	public void insert(Playlist playList) {
		sqlSession.insert("playlist.save", playList);
	}

	@Override
	public void update(Playlist playList) {
		sqlSession.update("playlist.updatePlaylist", playList);
	}

	@Override
	public void delete(String id) {
		sqlSession.delete("playlist.deletePlaylist", id);
	}

	@Override
	public Playlist getPlaylistByPlaylistId(String playlistId) {
		int id = Integer.parseInt(playlistId);
		Playlist playList = sqlSession.selectOne("playlist.getPlaylistById", id);
		return playList;
	}

	@Override
	public List<Playlist> getAllPlaylists() {
		List<Playlist> playList = sqlSession.selectList("playlist.getAllPlaylists");
		return playList;
	}

	@Override
	public List<Playlist> getPlaylistsByUserId(String userId) {
		List<Playlist> playList = sqlSession.selectList("playlist.getPlaylistsByUserId", userId);
		return playList;
	}

	@Override
	public List<Playlist> getPlaylistsByTitle(String title) {
		List<Playlist> playList = sqlSession.selectList("playlist.getPlaylistsByTitle", title);
		return playList;
	}

	@Override
	public List<Playlist> getPlaylistsWithLastThumbnailByUserId(String userId) {
		List<Playlist> playList = sqlSession.selectList("playlist.findPlaylistsWithLastThumbnail", userId);
		return playList;
	}

	@Override
	public void incrementViewCount(String id) {
		sqlSession.update("playlist.incrementViewCount", id);
	}

	@Override
	public void incrementLikeCount(String id) {
		sqlSession.update("playlist.incrementLikeCount", id);
	}

	@Override
	public void decrementLikeCount(String id) {
		sqlSession.update("playlist.decrementLikeCount", id);
	}
}
