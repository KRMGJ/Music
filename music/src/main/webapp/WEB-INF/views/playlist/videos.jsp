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
					<c:when test="${not empty playlist.lastVideoThumbnail}">
						<img class="playlist-thumbnail"
							src="${playlist.lastVideoThumbnail}" />
					</c:when>
					<c:otherwise>
						<img class="playlist-thumbnail"
							src="/resources/images/default-video-thumbnail.jpg" />
					</c:otherwise>
				</c:choose>


				<div id="playlistInfo" class="playlist-info"
					data-playlist-id="${playlist.id}">
					<h5 class="mb-1">${playlist.title}</h5>
					<p class="text-muted mb-2">게시자: ${playlist.nickname} · 영상
						${fn:length(playlistVideos)}개 · ${playlist.createdDate}</p>

					<div class="playlist-actions">
						<button id="btnPlayAll" class="btn dark" aria-label="모두 재생">▶
							재생</button>
						<button id="btnShare" class="btn light" aria-label="공유 링크 복사">↗
							공유</button>
					</div>
				</div>
			</div>
		</div>

		<div class="playlist-right">
			<div class="sort-bar">
				<div></div>
				<select id="sortSelect" class="sort-select">
					<option value="latest">최신순</option>
					<option value="oldest">오래된순</option>
					<option value="viewsDesc">조회수많은순</option>
					<option value="viewsAsc">조회수적은순</option>
					<option value="lengthDesc">길이긴순</option>
					<option value="lengthAsc">길이짧은순</option>
					<option value="titleAsc">이름순(A→Z)</option>
					<option value="titleDesc">이름역순(Z→A)</option>
				</select>
			</div>

			<c:choose>
				<c:when test="${playlistVideos == null || empty playlistVideos}">
					<p class="no-videos">이 플레이리스트에 영상이 없습니다.</p>
				</c:when>
				<c:otherwise>
					<div id="videoList">
						<c:forEach var="video" items="${playlistVideos}">
							<div class="video-card" data-published="${video.publishedDate}"
								data-duration="${video.durationInSeconds}"
								data-title="${fn:toLowerCase(video.title)}"
								data-views="${video.viewCount}">
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
													<input type="hidden" name="videoId"
														value="${video.videoId}" /> <input type="hidden"
														name="playlistId" value="${playlist.id}" />
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
										<img src="${video.channelThumbnail}" class="channel-thumbnail" />
										<span>${video.channelTitle}</span>
									</div>
									<div class="video-description">
										${fn:escapeXml(video.description)}</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

</body>
</html>
