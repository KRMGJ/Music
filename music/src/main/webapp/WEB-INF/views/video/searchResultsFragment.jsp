<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:choose>
	<c:when test="${searchResult == null || empty searchResult.videos}">
		<p class="text-muted">검색 결과가 없습니다.</p>
	</c:when>
	<c:otherwise>
		<c:forEach var="video" items="${searchResult.videos}">
			<div class="d-flex mb-4 video-item">
				<!-- 썸네일 -->
				<div class="video-thumbnail me-3 position-relative" data-video-id="${video.videoId}">
					<img src="${video.thumbnail}" alt="썸네일" class="thumbnail-img" />
					<div class="video-duration">${video.formattedDuration}</div>
				</div>

				<!-- 텍스트 영역 -->
				<div class="flex-grow-1 d-flex flex-column justify-content-between">
					<div class="d-flex justify-content-between align-items-start">
						<a href="https://www.youtube.com/watch?v=${video.videoId}" target="_blank" 
							class="text-decoration-none text-dark fw-bold video-title">${video.title}</a>

						<div class="dropdown ms-2">
							<button class="btn btn-sm btn-light dropdown-toggle"
								type="button" data-bs-toggle="dropdown" aria-expanded="false">
								⋮</button>
							<ul class="dropdown-menu">
								<li><a class="dropdown-item" href="#"
									onclick="openPlaylistModal('${video.videoId}')">플레이리스트에 추가</a></li>
								<li><a class="dropdown-item"
									href="https://www.youtube.com/watch?v=${video.videoId}"
									target="_blank">공유</a></li>
							</ul>
						</div>
					</div>

					<div class="text-muted mt-1 video-meta">조회수 ${video.formattedViewCount} ·
						<fmt:formatDate value="${video.publishedDate}" pattern="yyyy년 M월 d일" />
					</div>

					<div class="d-flex align-items-center my-1 channel-info">
						<img src="${video.channelInfo.channelThumbnail}" alt="채널썸네일" class="channel-thumbnail" /> 
						<span>${video.channelInfo.channelTitle}</span>
					</div>

					<div class="text-muted video-description">${video.description}</div>
				</div>
			</div>

			<!-- 모달 -->
			<div class="modal fade" id="playlistModal" tabindex="-1"
				aria-labelledby="playlistModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">
						<form id="addToPlaylistForm">
							<input type="hidden" name="videoId" id="modalVideoId" required />
							<div class="modal-header">
								<h5 class="modal-title" id="playlistModalLabel">플레이리스트에 추가</h5>
								<button type="button" class="btn-close" data-bs-dismiss="modal"
									aria-label="닫기"></button>
							</div>
							<div class="modal-body">
								<c:forEach var="playlist" items="${playlists}">
									<div class="form-check">
										<input class="form-check-input" type="radio" name="playlistId"
											id="playlistId" value="${playlist.id}" required>
										<label class="form-check-label" for="pl-${playlist.id}">
											${playlist.title} </label>
									</div>
								</c:forEach>
							</div>
							<div class="modal-footer">
								<button type="submit" class="btn btn-primary">추가</button>
							</div>
						</form>
					</div>
				</div>
			</div>

			<div id="player-${video.videoId}" class="mt-3"></div>
		</c:forEach>
	</c:otherwise>
</c:choose>
<link rel="stylesheet" href="/resources/css/video/searchResult.css"/>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="/resources/js/video/searchResults.js"></script>
