package com.example.music.dao.daoImpl;

import com.example.music.model.Playlist;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.music.dao.PlaylistDao;

import java.util.List;

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
    public void delete(int id) {
        sqlSession.delete("playlist.deletePlaylist", id);
    }

    @Override
    public Playlist getPlaylistByPlaylistId(int id) {
        Playlist playList = sqlSession.selectOne("playlist.getPlaylistById", id);
        return playList;
    }

    @Override
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playList = sqlSession.selectList("playlist.getAllPlaylists");
        return playList;
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(int userId) {
        List<Playlist> playList = sqlSession.selectList("playlist.getPlaylistsByUserId", userId);
        return playList;
    }

    @Override
    public List<Playlist> getPlaylistsByTitle(String title) {
        List<Playlist> playList = sqlSession.selectList("playlist.getPlaylistsByTitle", title);
        return playList;
    }

    @Override
    public List<Playlist> getPlaylistsWithLastThumbnailByUserId(int userId) {
        List<Playlist> playList = sqlSession.selectList("playlist.findPlaylistsWithLastThumbnail", userId);
        return playList;
    }

    @Override
    public void incrementViewCount(int id) {
        sqlSession.update("playlist.incrementViewCount", id);
    }

    @Override
    public void incrementLikeCount(int id) {
        sqlSession.update("playlist.incrementLikeCount", id);
    }

    @Override
    public void decrementLikeCount(int id) {
        sqlSession.update("playlist.decrementLikeCount", id);
    }
}
