// 플레이리스트에 비디오 추가 (jQuery)
$('#addToPlaylistForm').on('submit', function(event) {
	event.preventDefault(); // 기본 제출 막기

	const videoId = $('#modalVideoId').val();
	const playlistId = $('#playlistId').val();

	$.ajax({
		url: '/playlist/addVideo',
		method: 'POST',
		contentType: 'application/x-www-form-urlencoded',
		data: {
			playlistId: playlistId,
			videoId: videoId
		},
		success: function(response) {
			alert(response); // 서버에서 전달된 메시지 출력
		},
		error: function(error) {
			alert('Error adding video to playlist');
			console.error('Error details:', error);
		}
	});
});