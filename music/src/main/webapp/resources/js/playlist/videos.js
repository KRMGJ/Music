function toggleMenu(button) {
	const $menu = $(button).next('.dropdown-menu');
	const isVisible = $menu.is(':visible');

	$('.dropdown-menu').hide(); // 다른 메뉴 닫기
	if (!isVisible) {
		$menu.show(); // 현재 메뉴만 열기
	}
}

function shareVideo(videoId) {
	const url = `https://www.youtube.com/watch?v=${videoId}`;
	navigator.clipboard.writeText(url).then(() => {
		alert("공유 링크가 복사되었습니다:\n" + url);
	});
}

// 외부 클릭 시 드롭다운 닫기
$(document).on('click', function(e) {
	if (!$(e.target).closest('.options-wrapper').length) {
		$('.dropdown-menu').hide();
	}
});

(function() {
	const comparators = {
		latest: (a, b) => new Date($(b).data('published')) - new Date($(a).data('published')),
		oldest: (a, b) => new Date($(a).data('published')) - new Date($(b).data('published')),
		viewsDesc: (a, b) => (+$(b).data('views')) - (+$(a).data('views')),
		viewsAsc: (a, b) => (+$(a).data('views')) - (+$(b).data('views')),
		lengthDesc: (a, b) => (+$(b).data('duration')) - (+$(a).data('duration')),
		lengthAsc: (a, b) => (+$(a).data('duration')) - (+$(b).data('duration')),
		titleAsc: (a, b) => String($(a).data('title')).localeCompare(String($(b).data('title'))),
		titleDesc: (a, b) => String($(b).data('title')).localeCompare(String($(a).data('title')))
	};

	function sortVideoList(key) {
		const $list = $('#videoList');
		if ($list.length === 0) return;

		const items = $list.children('.video-card').get();
		const cmp = comparators[key] || comparators.latest;

		items.sort(cmp);
		// 재배치 (DOM 재정렬)
		$list.append(items);

		try { localStorage.setItem('videos_sort_key', key); } catch (e) { }
	}

	// 초기화: 저장된 정렬 키 적용
	$(function() {
		const saved = localStorage.getItem('videos_sort_key') || 'latest';
		$('#sortSelect').val(saved);
		sortVideoList(saved);

		$('#sortSelect').on('change', function() {
			sortVideoList(this.value);
		});
	});
})();

$(function() {
	var $info = $('#playlistInfo');
	if ($info.length === 0) return;
	var pid = $info.data('playlist-id');

	// ▶ 재생
	$('#btnPlayAll').on('click', function() {
		var $btn = $(this).prop('disabled', true);
		$.ajax({
			url: '/api/playlists/' + pid + '/videos',
			method: 'GET',
			dataType: 'json'
		}).done(function(list) {
			list = list || [];
			var ids = $.map(list, function(v) { return v.id; });
			if (!ids.length) { alert('영상이 없습니다.'); return; }
			location.href = '/video/' + ids[0] + '?queue=' + encodeURIComponent(ids.join(','));
		}).fail(function() {
			alert('재생 목록을 가져오지 못했어요.');
		}).always(function() {
			$btn.prop('disabled', false);
		});
	});

	// ↗ 공유
	$('#btnShare').on('click', function() {
		var $btn = $(this).prop('disabled', true);
		var shareUrl = location.href; // 기본값(백엔드 없을 때)

		$.ajax({
			url: '/api/playlists/' + pid + '/share',
			method: 'POST',
			dataType: 'json'
		}).done(function(res) {
			if (res && res.url) shareUrl = location.origin + res.url;
		}).always(function() {
			// Clipboard API 우선 사용, 실패 시 폴백
			if (navigator.clipboard && navigator.clipboard.writeText) {
				navigator.clipboard.writeText(shareUrl).then(function() {
					alert('공유 링크를 복사했어요.');
					$btn.prop('disabled', false);
				}, function() {
					fallbackCopy(shareUrl);
					$btn.prop('disabled', false);
				});
			} else {
				fallbackCopy(shareUrl);
				$btn.prop('disabled', false);
			}
		});

		function fallbackCopy(text) {
			var $temp = $('<input>').val(text).appendTo('body').select();
			try {
				document.execCommand('copy');
				alert('공유 링크를 복사했어요.');
			} catch (e) {
				alert('복사 실패. 링크: ' + text);
			}
			$temp.remove();
		}
	});
});