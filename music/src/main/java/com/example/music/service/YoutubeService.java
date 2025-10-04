package com.example.music.service;

import java.util.List;

import com.example.music.model.CommentPostRequest;
import com.example.music.model.Comments;
import com.example.music.model.PageResponse;
import com.example.music.model.SearchList;
import com.example.music.model.Video;
import com.example.music.model.VideoDetail;
import com.example.music.model.VideoListItem;
import com.example.music.model.YoutubePlaylist;
import com.example.music.service.serviceImpl.YoutubeServiceImpl.RelatedResponse;

public interface YoutubeService {
	SearchList searchVideos(String query, int page, String sort, String duration, String upload, String regionCode);

	Video fetchAndSaveVideoById(String videoId);

	List<Video> paginate(List<Video> videos, int page, int pageSize);

	List<Video> getVideosByChannel(String channelTitle);

	VideoDetail getDetail(String id);

	List<VideoListItem> getRelated(String id, int limit);

	RelatedResponse getRelatedPage(String id, String pageToken, int size);

	Comments.Page getComments(String videoId, String pageToken, String order, Integer pageSize);

	Object postComment(CommentPostRequest req, String accessToken) throws Exception;

	PageResponse<YoutubePlaylist> getMyPlaylists(String accessToken, int size, String pageToken);
}
