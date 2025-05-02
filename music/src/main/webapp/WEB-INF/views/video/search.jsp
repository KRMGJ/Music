<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
    <title>YouTube 검색</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/resources/css/video/search.css">
    <!-- Bootstrap JS + Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body class="container py-4">
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />

    <!-- 📺 검색 결과 -->
    <jsp:include page="/WEB-INF/views/video/searchResultsFragment.jsp" />

    <!-- ⏩ 페이지 버튼 -->
    <c:if test="${searchResult.totalPages > 1}">
        <div class="d-flex justify-content-center mt-4">
            <c:forEach var="i" begin="1" end="${searchResult.totalPages}">
                <a href="/video/search?query=${param.query}&channel=${param.channel}&filter=${param.filter}&sort=${param.sort}&page=${i}"
                   class="btn mx-1 ${searchResult.currentPage == i ? 'btn-primary' : 'btn-outline-secondary'}">
                    ${i}
                </a>
            </c:forEach>
        </div>
    </c:if>

    <!-- 모달 -->
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
                                <input class="form-check-input" type="radio" name="playlistId" id="pl-${playlist.id}" value="${playlist.id}" required>
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
