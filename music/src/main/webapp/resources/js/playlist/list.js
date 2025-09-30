$(function() {
	// 검색 엔터 제출
	$('.search-input').on('keydown', function(e) {
		if (e.key === 'Enter') $('#searchForm').submit();
	});

	// 정렬 변경 시 자동 제출
	$('#sortSelect').on('change', function() {
		$('#sortForm').submit();
	});

	// 새 플레이리스트 생성
	$('#addPlaylistForm').on('submit', function(e) {
		e.preventDefault();
		var title = $.trim($('#createTitle').val());
		if (!title) return;

		$.ajax({
			type: 'POST',
			url: '/playlist/create',
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			data: { title: title } // ← 마지막 쉼표 제거
		})
			.done(function() { location.reload(); })
			.fail(function() { alert('플레이리스트 생성 실패'); });
	});

	// 카드 내부 공통 헬퍼
	function getIdFromButton(btn) {
		return $(btn).closest('.playlist-card').data('pl-id');
	}

	// 공유
	$(document).on('click', '.js-share', function() {
		var id = getIdFromButton(this);
		var url = location.origin + '/playlist/' + id;

		if (navigator.clipboard && navigator.clipboard.writeText) {
			navigator.clipboard.writeText(url)
				.then(function() { alert('공유 링크가 복사되었습니다:\n' + url); })
				.catch(function() { alert('복사 실패. 주소창에서 직접 복사해 주세요.'); });
		} else {
			alert('공유 링크: ' + url);
		}
	});

	// 이름 수정
	$(document).on('click', '.js-rename', function() {
		var id = getIdFromButton(this);
		var next = window.prompt('새 제목을 입력하세요.');
		if (!next) return;

		$.ajax({
			type: 'POST',
			url: '/playlist/rename',
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			data: { id: id, title: $.trim(next) } // ← 축약표현 대신 명시적 키:값
		})
			.done(function() { location.reload(); })
			.fail(function() { alert('제목 변경 실패'); });
	});

	// 삭제
	$(document).on('click', '.js-delete', function() {
		var id = getIdFromButton(this);
		if (!window.confirm('이 플레이리스트를 삭제할까요?')) return;

		$.ajax({
			type: 'POST', // 서버가 DELETE 미지원이면 POST 유지
			url: '/playlist/delete',
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			data: { id: id }
		})
			.done(function() { location.reload(); })
			.fail(function() { alert('삭제 실패'); });
	});
});
