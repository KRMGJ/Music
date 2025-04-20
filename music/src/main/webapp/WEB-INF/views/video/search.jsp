<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>YouTube 검색</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style>
        .card-title { font-size: 1.2rem; }
        .badge-shorts { background-color: red; color: white; font-size: 0.75rem; margin-left: 5px; }
    </style>
</head>
<body class="container py-4">

    <!-- 🔍 검색 폼 -->
    <form method="get" action="/video/search" class="row g-2 mb-4">
        <div class="col-md-4">
            <input type="text" name="query" placeholder="검색어" class="form-control" value="${param.query}" />
        </div>
        <div class="col-md-3">
            <input type="text" name="channel" placeholder="채널명 (선택)" class="form-control" value="${param.channel}" />
        </div>
        <div class="col-md-3">
            <select name="filter" class="form-select">
                <option value="all" ${param.filter == 'all' ? 'selected' : ''}>전체</option>
                <option value="shorts" ${param.filter == 'shorts' ? 'selected' : ''}>쇼츠</option>
                <option value="videos" ${param.filter == 'videos' ? 'selected' : ''}>일반 영상</option>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary w-100">검색</button>
        </div>
    </form>

    <!-- 📺 영상 리스트 -->
    <c:forEach var="video" items="${videos}">
        <div class="card mb-3">
            <div class="row g-0">
                <div class="col-md-4">
                    <a href="https://www.youtube.com/watch?v=${video.id}" target="_blank">
                        <img src="${video.thumbnail}" class="img-fluid rounded-start" alt="썸네일" />
                    </a>
                </div>
                <div class="col-md-8">
                    <div class="card-body">
                        <h5 class="card-title">
                            ${video.title}
                            <c:if test="${video.shorts}">
                                <span class="badge badge-shorts">쇼츠</span>
                            </c:if>
                        </h5>
                        <p class="card-text text-muted mb-1">채널: ${video.channelTitle}</p>
                        <p class="card-text">
                            등록일: <fmt:formatDate value="${video.publishedDate}" pattern="yyyy-MM-dd HH:mm" />
                        </p>
                        <a href="https://www.youtube.com/watch?v=${video.id}" class="btn btn-sm btn-outline-primary" target="_blank">영상 보기</a>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>

    <!-- ⏩ 페이지 버튼 -->
    <div class="d-flex justify-content-center mt-4">
        <c:forEach var="i" begin="1" end="5">
            <a href="/video/search?query=${param.query}&channel=${param.channel}&filter=${param.filter}&page=${i}"
               class="btn btn-outline-secondary mx-1 ${param.page == i ? 'active' : ''}">
                ${i}
            </a>
        </c:forEach>
    </div>

</body>
</html>
