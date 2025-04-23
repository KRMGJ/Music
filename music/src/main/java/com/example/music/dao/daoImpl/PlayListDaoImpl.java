package com.example.music.dao.daoImpl;

import com.example.music.model.PlayList;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.music.dao.PlayListDao;

import java.util.List;

@Repository
public class PlayListDaoImpl implements PlayListDao {

    @Autowired
    SqlSession sqlSession;

    @Override
    public void insert(PlayList playList) {
        sqlSession.insert("playlist.save", playList);
    }

    @Override
    public void update(PlayList playList) {
        sqlSession.update("playlist.updatePlaylist", playList);
    }

    @Override
    public void delete(int id) {
        sqlSession.delete("playlist.deletePlaylist", id);
    }

    @Override
    public PlayList getPlaylistByPlaylistId(int id) {
        PlayList playList = sqlSession.selectOne("playlist.getPlaylistById", id);
        return playList;
    }

    @Override
    public List<PlayList> getAllPlaylists() {
        List<PlayList> playList = sqlSession.selectList("playlist.getAllPlaylists");
        return playList;
    }

    @Override
    public List<PlayList> getPlaylistsByUserId(int userId) {
        List<PlayList> playList = sqlSession.selectList("playlist.getPlaylistsByUserId", userId);
        return playList;
    }

    @Override
    public List<PlayList> getPlaylistsByTitle(String title) {
        List<PlayList> playList = sqlSession.selectList("playlist.getPlaylistsByTitle", title);
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
