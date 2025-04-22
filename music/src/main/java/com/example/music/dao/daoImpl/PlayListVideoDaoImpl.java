package com.example.music.dao.daoImpl;

import com.example.music.dao.PlayListVideoDao;
import com.example.music.model.PlaylistVideo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlayListVideoDaoImpl implements PlayListVideoDao {

    @Autowired
    SqlSession sqlSession;

    @Override
    public void insert(int playlistId, String videoId) {
        PlaylistVideo pv = new PlaylistVideo(playlistId, videoId);
        sqlSession.insert("playlistVideo.insert", pv);
    }

    @Override
    public List<String> getVideosByPlaylistId(int playlistId) {
        List<String> videoIds = sqlSession.selectList("playlistVideo.getVideosByPlaylistId", playlistId);
        return videoIds;
    }
}
