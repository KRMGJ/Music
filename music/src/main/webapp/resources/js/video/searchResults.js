// 플레이리스트에 비디오 추가
document.getElementById("addToPlaylistForm").addEventListener("submit", function(event) {
	event.preventDefault();  // 폼 제출을 막고 Ajax 요청을 보냄

	var videoId = document.getElementById('modalVideoId').value;
	var playlistId = document.getElementById('playlistId').value;


	fetch("/playlist/addVideo", {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({
			playlistId: playlistId,
			videoId: videoId
		})
	}).then(response => {
		if (response.ok) {
			return response.text();  // 서버에서 보낸 성공 메시지를 반환
		}
		return Promise.reject('Failed to add video');
	}).then(successMessage => {
		alert(successMessage);  // "Video added to playlist successfully" 메시지
	}).catch(error => {
		alert(error);  // "Error adding video to playlist" 메시지
	});
});