// 비디오 검색 관련
function openPlaylistModal(videoId) {
	$('#modalVideoId').val(videoId);
	const modal = new bootstrap.Modal(document.getElementById('playlistModal'));
	modal.show();
}

// 필터 변경 시 AJAX로 검색 결과 갱신
$('input[name="upload"], input[name="duration"], input[name="sort"]').on('change', function () {
	const query = $('input[name="query"]').val();
	const sort = $('input[name="sort"]:checked').val();
	const duration = $('input[name="duration"]:checked').val();
	const upload = $('input[name="upload"]:checked').val();

	$.ajax({
		url: `/video/search`,
		method: 'GET',
		data: {
			query: query,
			sort: sort,
			duration: duration,
			upload: upload,
			page: 1
		},
		success: function (data) {
			$('#searchResults').html(data);
		},
		error: function (error) {
			console.error('Error fetching search results:', error);
		}
	});
});
