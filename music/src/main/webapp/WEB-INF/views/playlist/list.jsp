<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>My Playlists</title>
<link rel="stylesheet" href="/resources/css/playlist/list.css">
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="playlist-container">
		<div class="playlist-grid">
		    <c:choose>
                <c:when test="${empty playlists}">
                    <p class="text-muted">재생목록이 없습니다.</p>
                </c:when>
                <c:otherwise>
                    <p class="text-muted">총 ${playlists.size()}개의 재생목록이 있습니다.</p>
                </c:otherwise>
            </c:choose>
			<c:forEach var="playlist" items="${playlists}">
				<div class="playlist-card">
					<div class="thumbnail-wrapper">
						<c:choose>
							<c:when test="${not empty playlist.lastVideoThumbnail}">
								<img class="thumbnail" src="${playlist.lastVideoThumbnail}"
									alt="Thumbnail">
							</c:when>
							<c:otherwise>
								<img class="thumbnail"
									src="/resources/images/default-video-thumbnail.jpg"
									alt="Thumbnail">
							</c:otherwise>
						</c:choose>
						<div class="video-count">동영상 ${playlist.videoCount}개</div>
					</div>
					<div class="playlist-info">
						<div class="playlist-title">${playlist.playlistTitle}</div>
						<div class="playlist-meta">조회수 ${playlist.viewCount} · 좋아요
							${playlist.likeCount}</div>
						<div class="playlist-date">${playlist.createdDate}</div>
						<div class="view-all-link">
							<a href="/playlist/videos?playlistId=${playlist.playlistId}">모든
								재생목록 보기</a>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>

</body>
</html>
