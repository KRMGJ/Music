<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<title>유저정보 수정</title>
</head>
<body>
	<div class="container">
		<form action="<c:url value='/user/updateUser'/>" method="post">
			<input type="hidden" name="userId" value="${user.userId}" />
			<div class="mb-3">
				<label for="nickname" class="form-label">이름</label> <input
					type="text" class="form-control" id="nickname" name="nickname"
					value="${user.nickname}" required>
			</div>
			<div class="mb-3">
				<label for="email" class="form-label">이메일</label> <input
					type="email" class="form-control" id="email" name="email"
					value="${user.email}" required>
			</div>
			<button type="submit" class="btn btn-primary">수정하기</button>
		</form>
	</div>
</body>
</html>