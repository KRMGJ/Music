$(document).ready(function () {
	$("#addPlaylistForm").on("submit", function (event) {
		event.preventDefault(); // 기본 폼 제출 막기

		const title = $("#title").val();

		$.ajax({
			type: "POST",
			url: "/playlist/create",
			contentType: "application/x-www-form-urlencoded",
			data: { title: title },
			success: function () {
				alert("플레이리스트 생성 성공");
				location.reload();
			},
			error: function (error) {
				console.error("에러 발생:", error);
				alert("플레이리스트 생성 실패");
			}
		});
	});
});
