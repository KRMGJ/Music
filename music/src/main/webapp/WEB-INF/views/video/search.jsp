<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
    <title>YouTube 검색</title>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/resources/css/video/search.css">
    <!-- Bootstrap JS + Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="container py-4">
    <!-- 📺 검색 결과 -->
    <jsp:include page="/WEB-INF/views/video/fragment/searchResultsFragment.jsp" />

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
    </div>
    <script src="/resources/js/video/search.js"></script>
</body>
</html>
