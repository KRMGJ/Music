<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/playlist/videos.css">
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="/resources/js/playlist/videos.js"></script>
<title>video list</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<c:if test="${not empty successMessage}">
		<div class="message success">${successMessage}</div>
	</c:if>

	<c:if test="${not empty errorMessage}">
		<div class="message error">${errorMessage}</div>
	</c:if>

	<div class="playlist-container">
		<div class="playlist-left">
			<div class="playlist-card">
				<c:choose>
					<c:when test="${not empty playlist.image}">
						<img class="playlist-thumbnail" src="${playlist.image}" />
					</c:when>
					<c:when test="${not empty playlist.lastVideoThumbnail}">
						<img class="playlist-thumbnail"
							src="${playlist.lastVideoThumbnail}" />
					</c:when>
					<c:otherwise>
						<img class="playlist-thumbnail"
							src="/resources/images/default-video-thumbnail.jpg" />
					</c:otherwise>
				</c:choose>


				<div class="playlist-info">
					<h5>${playlist.title}</h5>
					<p>게시자: ${playlist.userId}</p>
					<p>영상 ${fn:length(playlistVideos)}개</p>
					<p>조회수 ${playlist.viewCount}</p>
					<p>좋아요 ${playlist.likeCount}</p>
					<p>생성일 ${playlist.createdDate}</p>
					<div class="playlist-buttons">
						<button class="btn dark">▶ 모두 재생</button>
						<button class="btn light">+ 추가</button>
						<button class="btn light">✏️ 편집</button>
						<button class="btn light">↗ 공유</button>
					</div>
				</div>
			</div>
		</div>

		<div class="playlist-right">
			<div class="sort-bar">
				<h5>정렬</h5>
				<select onchange="location.href=this.value" class="sort-select">
					<option value="?sort=custom"
						${selectedSortLabel == '직접' ? 'selected' : ''}>직접</option>
					<option value="?sort=addedDesc"
						${selectedSortLabel == '추가된 날짜(최신순)' ? 'selected' : ''}>추가된
						날짜(최신순)</option>
					<option value="?sort=addedAsc"
						${selectedSortLabel == '추가된 날짜(오래된순)' ? 'selected' : ''}>추가된
						날짜(오래된순)</option>
					<option value="?sort=popular"
						${selectedSortLabel == '인기순' ? 'selected' : ''}>인기순</option>
					<option value="?sort=dateDesc"
						${selectedSortLabel == '게시일(최신순)' ? 'selected' : ''}>게시일(최신순)</option>
					<option value="?sort=dateAsc"
						${selectedSortLabel == '게시일(오래된순)' ? 'selected' : ''}>게시일(오래된순)</option>
				</select>
			</div>

			<c:choose>
				<c:when test="${playlistVideos == null || empty playlistVideos}">
					<p class="no-videos">이 플레이리스트에 영상이 없습니다.</p>
				</c:when>
				<c:otherwise>
					<c:forEach var="video" items="${playlistVideos}">
						<div class="video-card">
							<div class="video-thumbnail-container">
								<a href="https://www.youtube.com/watch?v=${video.videoId}"
									target="_blank"> <img src="${video.thumbnail}" alt="썸네일"
									class="video-thumbnail" />
									<div class="video-duration">${video.formattedDuration}</div>
								</a>
							</div>
							<div class="video-info">
								<div class="video-title-container">
									<a href="https://www.youtube.com/watch?v=${video.videoId}"
										target="_blank" class="video-title">
										${fn:escapeXml(video.title)} </a>
									<div class="options-wrapper">
										<button class="more-btn" onclick="toggleMenu(this)">⋮</button>
										<div class="dropdown-menu">
											<form action="/playlist/remove" method="post">
												<input type="hidden" name="videoId" value="${video.videoId}" />
												<input type="hidden" name="playlistId"
													value="${playlist.id}" />
												<button type="submit" class="menu-item">리스트에서 삭제</button>
											</form>
											<button class="menu-item"
												onclick="shareVideo('${video.videoId}')">공유</button>
										</div>
									</div>
								</div>
								<div class="video-meta">
									조회수 ${video.formattedViewCount} ·
									<fmt:formatDate value="${video.publishedDate}"
										pattern="yyyy년 M월 d일" />
								</div>
								<div class="video-channel">
									<img src="${video.channelInfo.channelThumbnail}"
										class="channel-thumbnail" /> <span>${video.channelInfo.channelTitle}</span>
								</div>
								<div class="video-description">
									${fn:escapeXml(video.description)}</div>
							</div>
						</div>
					</c:forEach>

				</c:otherwise>
			</c:choose>
		</div>
	</div>

</body>
</html>
