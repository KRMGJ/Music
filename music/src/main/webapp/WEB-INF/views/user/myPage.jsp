<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<title>내 정보</title>
</head>
<body>
	<div class="container">
		<p>아이디: ${user.userId}</p>
		<p>이름: ${user.nickname}</p>
		<p>이메일: ${user.email}</p>

		<a href="<c:url value='/user/updateUser'/>" class="btn btn-primary">정보수정</a>
		<a href="<c:url value='/user/deleteUser'/>" class="btn btn-danger">회원탈퇴</a>
	</div>
</body>
</html>