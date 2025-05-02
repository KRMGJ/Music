<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                        <a href="https://www.youtube.com/watch?v=${video.videoId}"
                           target="_blank"
                           class="text-decoration-none text-dark fw-bold video-title">
                           ${video.title}
                        </a>

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

                    <div class="text-muted mt-1 video-meta">
                        조회수 ${video.formattedViewCount} ·
                        <fmt:formatDate value="${video.publishedDate}" pattern="yyyy년 M월 d일" />
                    </div>

                    <div class="d-flex align-items-center my-1 channel-info">
                        <img src="${video.channelInfo.channelThumbnail}" alt="채널썸네일" class="channel-thumbnail" />
                        <span>${video.channelInfo.channelTitle}</span>
                    </div>

                    <div class="text-muted video-description">
                        ${video.description}
                    </div>
                </div>
            </div>

            <div id="player-${video.videoId}" class="mt-3"></div>
        </c:forEach>
    </c:otherwise>
</c:choose>
