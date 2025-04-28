<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<title>YouTube 검색</title>
<!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Bootstrap JS + Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<style>
.video-duration {
	position: absolute;
	bottom: 5px;
	right: 8px;
	background-color: rgba(0, 0, 0, 0.75);
	color: white;
	font-size: 0.75rem;
	padding: 2px 5px;
	border-radius: 4px;
}

.badge-shorts {
	background-color: red;
	color: white;
	font-size: 0.7rem;
	height: fit-content;
	margin-left: 10px;
}

.video-description {
	font-size: 0.85rem;
	max-height: 2.6em; /* 두 줄 제한 */
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}
</style>

</head>
<body class="container py-4">
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<!-- 📺 검색 결과 -->
	<c:choose>
		<c:when test="${searchResult == null || empty searchResult.videos}">
			<p class="text-muted">검색 결과가 없습니다.</p>
		</c:when>
		<c:otherwise>
			<c:forEach var="video" items="${searchResult.videos}">
				<div class="d-flex mb-4" style="height: 120px;">
					<!-- 썸네일 -->
					<div class="video-thumbnail me-3 position-relative" style="flex-shrink: 0;" data-video-id="${video.videoId}">
					    <img src="${video.thumbnail}" alt="썸네일"
					        style="width: 210px; height: 120px; object-fit: cover; border-radius: 8px;" />
					        <div class="video-duration">${video.formattedDuration}</div>
					</div>

					<!-- 텍스트 영역 -->
					<div class="flex-grow-1 d-flex flex-column justify-content-between">
					    
					    <!-- 제목 + 더보기 버튼 (같은 줄) -->
					    <div class="d-flex justify-content-between align-items-start">
					        <!-- 제목 -->
					        <a href="https://www.youtube.com/watch?v=${video.videoId}"
					           target="_blank"
					           class="text-decoration-none text-dark fw-bold"
					           style="font-size: 1rem; flex: 1;">
					           ${fn:escapeXml(video.title)}
					        </a>
					
					        <!-- 더보기 버튼 -->
					        <div class="dropdown ms-2">
					            <button class="btn btn-sm btn-light dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
					                ⋮
					            </button>
					            <ul class="dropdown-menu">
					                <li><a class="dropdown-item" href="#" onclick="openPlaylistModal('${video.videoId}')">플레이리스트에 추가</a></li>
					                <li><a class="dropdown-item" href="https://www.youtube.com/watch?v=${video.videoId}" target="_blank">공유</a></li>
					            </ul>
					        </div>
					    </div>
					    
					    <!-- 조회수, 업로드일 -->
					    <div class="text-muted mt-1" style="font-size: 0.85rem;">
					        조회수 ${video.formattedViewCount} ·
					        <fmt:formatDate value="${video.publishedDate}" pattern="yyyy년 M월 d일" />
					    </div>
					
					    <!-- 채널 정보 -->
					    <div class="d-flex align-items-center my-1" style="font-size: 0.9rem;">
					        <img src="${video.channelInfo.channelThumbnail}" alt="채널썸네일"
					             style="width: 24px; height: 24px; border-radius: 50%; object-fit: cover; margin-right: 8px;" />
					        <span>${video.channelInfo.channelTitle}</span>
					    </div>
					
					    <!-- 설명 -->
					    <div class="text-muted video-description">
					        ${fn:escapeXml(video.description)}
					    </div>
					</div>
				</div>
				<!-- 플레이어 -->
    			<div id="player-${video.videoId}" class="mt-3"></div>
			</c:forEach>
		</c:otherwise>
	</c:choose>

	<!-- ⏩ 페이지 버튼 -->
	<c:if test="${searchResult.totalPages > 1}">
		<div class="d-flex justify-content-center mt-4">
			<c:forEach var="i" begin="1" end="${searchResult.totalPages}">
				<a href="/video/search?query=${param.query}&channel=${param.channel}&filter=${param.filter}&sort=${param.sort}&page=${i}"
					class="btn btn-outline-secondary mx-1 ${searchResult.currentPage == i ? 'btn-primary' : 'btn-outline-secondary'}">
					${i} 
				</a>
			</c:forEach>
		</div>
	</c:if>

	<!-- 플레이리스트 선택 모달 -->
    <div class="modal fade" id="playlistModal" tabindex="-1" aria-labelledby="playlistModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form id="addToPlaylistForm">
	                <input type="hidden" name="videoId" id="modalVideoId" required />
	                <div class="modal-header">
	                    <h5 class="modal-title" id="playlistModalLabel">플레이리스트에 추가</h5>
	                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
	                </div>
	                <div class="modal-body">
	                    <c:forEach var="playlist" items="${playlists}">
	                        <div class="form-check">
	                            <input class="form-check-input" type="radio" name="playlistId" id="playlistId" value="${playlist.id}" required>
	                            <label class="form-check-label" for="pl-${playlist.id}">
	                                ${playlist.title}
	                            </label>
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
<script src="/resources/js/video/search.js"></script>
</body>
</html>
