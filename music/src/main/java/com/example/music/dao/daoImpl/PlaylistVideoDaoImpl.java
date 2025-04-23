package com.example.music.dao.daoImpl;

import com.example.music.dao.PlaylistVideoDao;
import com.example.music.model.Playlist;
import com.example.music.model.PlaylistVideo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlaylistVideoDaoImpl implements PlaylistVideoDao {

    @Autowired
    SqlSession sqlSession;

    @Override
    public void insert(int playlistId, String videoId) {
        PlaylistVideo pv = new PlaylistVideo(playlistId, videoId);
        sqlSession.insert("playlistVideo.insert", pv);
    }

    @Override
    public Playlist getPlaylistByPlaylistId(int playlistId) {
        Playlist playList = sqlSession.selectOne("playlistVideo.getPlaylistByPlaylistId", playlistId);
        return playList;
    }

    @Override
    public List<String> getVideosByPlaylistId(int playlistId) {
        List<String> videoIds = sqlSession.selectList("playlistVideo.getVideosByPlaylistId", playlistId);
        return videoIds;
    }

    @Override
    public void deleteVideoFromPlaylist(PlaylistVideo pv) {
        sqlSession.delete("playlistVideo.deleteVideoFromPlaylist", pv);
    }
}
