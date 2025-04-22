<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<title>로그인</title>
</head>
<body>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

	<div class="container">
		<form action="<c:url value='/auth/login'/>" method="post">
			<div class="mb-3">
				<label for="userId" class="form-label">아이디</label> <input
					type="text" class="form-control" id="userId" name="userId" required>
			</div>
			<div class="mb-3">
				<label for="password" class="form-label">비밀번호</label> <input
					type="password" class="form-control" id="password" name="password"
					required>
			</div>
			<button type="submit" class="btn btn-primary">로그인</button>
		</form>
	</div>
</body>
</html>