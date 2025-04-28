document.getElementById("addPlaylistForm").addEventListener("submit", function(event) {
	event.preventDefault();  // 폼 제출을 막고 Ajax 요청을 보냄

	fetch("/playlist/create", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({
			title: document.getElementById('title').value
		})
	}).then(response => {
		if (response.ok) {
			return response.text().then(data => {
				alert("플레이리스트 생성 성공");
				window.location.reload();  // 페이지 새로고침
			}
			);
		} else {
			return response.text().then(errorMessage => {
				alert("플레이리스트 생성 실패");
			});
		}
	}).catch(error => {
		console.error("Error:", error);
		alert("플레이리스트 생성 중 오류 발생");
	});
});