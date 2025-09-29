package com.example.music.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Comments {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Reply {
		private String id;
		private String authorDisplayName;
		private String authorProfileImageUrl;
		private String textDisplay; // HTML 포함됨 (YouTube가 <br> 등 제공)
		private String publishedAt; // ISO8601
		private String updatedAt; // ISO8601
		private Long likeCount;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Comment {
		private String id;
		private String authorDisplayName;
		private String authorProfileImageUrl;
		private String textDisplay; // HTML 포함
		private String publishedAt;
		private String updatedAt;
		private Long likeCount;
		private Long totalReplyCount;
		private List<Reply> replies; // 일부만 올 수 있음 (API page size에 따름)
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Page {
		private List<Comment> items;
		private String nextPageToken;
		private Integer resultsPerPage;
		private Integer totalResults; // YouTube가 정확히 모든 total을 주진 않음. (pageInfo.totalResults)
		private Boolean commentsDisabled; // 댓글 사용중지 여부
		private String errorMessage; // 사용자 안내용 메시지
	}
}