<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
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
<title>video list</title>
</head>
<body>
	<jsp:include page="/WEB-INF/views/layout/header.jsp" />

    <c:if test="${not empty successMessage}">
        <div class="alert alert-info" role="alert">
            ${successMessage}
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger" role="alert">
            ${errorMessage}
        </div>
    </c:if>

	<!-- 플레이리스트 정보 -->
	<div class="playlist-header mb-4">
		<h2 class="text-dark">${playlist.title}</h2>
		<img src="${playlist.image}" alt="플레이리스트 썸네일"
			style="width: 150px; height: 150px; object-fit: cover; border-radius: 8px;">
        <p class="text-muted mt-2">${playlist.viewCount}</p>
        <p class="text-muted">${playlist.likeCount}</p>
        <p class="text-muted">${playlist.createdDate}</p>
	</div>

	<!-- 영상 목록 -->
	<c:choose>
		<c:when test="${playlistVideos == null || empty playlistVideos}">
			<p class="text-muted">이 플레이리스트에 영상이 없습니다.</p>
		</c:when>
		<c:otherwise>
			<c:forEach var="video" items="${playlistVideos}">
			    <div class="card mb-3 shadow-sm">
			        <div class="row g-0">
			            <!-- 썸네일 -->
			            <div class="col-auto position-relative">
			                <a href="https://www.youtube.com/watch?v=${video.videoId}" target="_blank">
			                    <img src="${video.thumbnail}" alt="썸네일"
			                         style="width: 210px; height: 120px; object-fit: cover; border-radius: 8px; margin: 10px;" />
			                    <div class="video-duration">${video.formattedDuration}</div>
			                </a>
			            </div>
			
			            <!-- 내용 -->
			            <div class="col d-flex flex-column justify-content-between p-2">
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
			                        <ul class="dropdown-menu dropdown-menu-end">
			                            <li><a class="dropdown-item" href="https://www.youtube.com/watch?v=${video.videoId}" target="_blank">공유</a></li>
			                            <form action="/playlist/remove" method="post">
                                            <input type="hidden" name="videoId" value="${video.videoId}" />
                                            <input type="hidden" name="playlistId" value="${playlist.id}" />
			                            <li><button class="dropdown-item text-danger">리스트에서 삭제</button></li>
			                            </form>
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
			    </div>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</body>
</html>