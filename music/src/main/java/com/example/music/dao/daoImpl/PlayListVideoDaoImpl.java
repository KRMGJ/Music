package com.example.music.dao.daoImpl;

import com.example.music.dao.PlayListVideoDao;
import com.example.music.model.PlaylistVideo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PlayListVideoDaoImpl implements PlayListVideoDao {

    @Autowired
    SqlSession sqlSession;

    @Override
    public void insert(int playlistId, String videoId) {
        PlaylistVideo pv = new PlaylistVideo(playlistId, videoId);
        sqlSession.insert("playlistVideo.insert", pv);
    }
}
