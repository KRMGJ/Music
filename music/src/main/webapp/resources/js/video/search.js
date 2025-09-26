// 비디오 검색 관련
function openPlaylistModal(videoId) {
	$('#modalVideoId').val(videoId);
	const modal = new bootstrap.Modal(document.getElementById('playlistModal'));
	modal.show();
}

// 비디오 플레이어 열기
function openPlayer(videoId) {
	const $playerDiv = $('#player-' + videoId);
	if ($playerDiv.length === 0) return;

	if ($.trim($playerDiv.html()) !== '') {
		$playerDiv.empty();
		return;
	}

	const iframe = $('<iframe>', {
		src: "https://www.youtube.com/embed/" + videoId + "?autoplay=1",
		title: "YouTube video player",
		allow: "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share",
		allowfullscreen: true,
		frameborder: "0",
		class: "w-100 h-100"
	});

	const ratioDiv = $('<div>', { class: 'ratio ratio-16x9' }).append(iframe);

	$playerDiv.empty().append(ratioDiv);

	$('html, body').animate({
		scrollTop: $playerDiv.offset().top - 80
	}, 500);
}

// 썸네일 클릭 시 비디오 플레이어 열기
$(document).ready(function () {
	$('.video-thumbnail').on('click', function (e) {
		let $target = $(e.target);
		let $container = $target.closest('[data-video-id]');

		const rawVideoId = $container.data('videoId');
		const videoId = rawVideoId ? rawVideoId.toString().trim() : null;


		if (!videoId) {
			console.warn('Invalid videoId');
			return;
		}

		openPlayer(videoId);
	});
});

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
