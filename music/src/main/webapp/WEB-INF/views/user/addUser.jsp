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
<title>회원 가입</title>
</head>
<body>
	<div class="container">
		<form action="<c:url value='/user/addUser'/>" method="post">
			<div class="mb-3">
				<label for="userId" class="form-label">아이디</label> <input
					type="text" class="form-control" id="userId" name="userId"
					required>
			</div>
			<div class="mb-3">
				<label for="password" class="form-label">비밀번호</label> <input
					type="password" class="form-control" id="password" name="password"
					required>
			</div>
			<div class="mb-3">
				<label for="email" class="form-label">이메일</label> <input
					type="email" class="form-control" id="email" name="email" required>
			</div>
			<div class="mb-3">
				<label for="nickname" class="form-label">이름</label> <input
					type="text" class="form-control" id="nickname" name="nickname" required>
			</div>
			<button type="submit" class="btn btn-primary">가입하기</button>
		</form>

	</div>
</body>
</html>
