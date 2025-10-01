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
			alert("플레이리스트 추가 완료");
		},
		error: function(error) {
			alert("플레이리스트 추가 실패");
			console.error('Error details:', error);
		}
	});
});