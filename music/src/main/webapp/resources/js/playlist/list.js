// jQuery 전제
$(function() {
	// 검색 엔터 제출
	$('.search-input').on('keydown', function(e) {
		if (e.key === 'Enter') $('#searchForm').trigger('submit');
	});

	// 정렬 변경 시 자동 제출
	$('#sortSelect').on('change', function() {
		$('#sortForm').trigger('submit');
	});

	// 새 플레이리스트 생성
	$('#addPlaylistForm').on('submit', function(e) {
		e.preventDefault();
		const title = $('#createTitle').val().trim();
		if (!title) return;

		$.ajax({
			type: 'POST',
			url: '/playlist/create',
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			data: { title },
		})
			.done(() => location.reload())
			.fail(() => alert('플레이리스트 생성 실패'));
	});

	// 카드 내부 공통 헬퍼
	function getIdFromButton(btn) {
		return $(btn).closest('.playlist-card').data('pl-id');
	}

	// 공유
	$(document).on('click', '.js-share', function() {
		const id = getIdFromButton(this);
		const url = location.origin + '/playlist/' + id;
		if (navigator.clipboard?.writeText) {
			navigator.clipboard
				.writeText(url)
				.then(() => alert('공유 링크가 복사되었습니다:\n' + url))
				.catch(() => alert('복사 실패. 주소창에서 직접 복사해 주세요.'));
		} else {
			alert('공유 링크: ' + url);
		}
	});

	// 이름 수정
	$(document).on('click', '.js-rename', function() {
		const id = getIdFromButton(this);
		const next = prompt('새 제목을 입력하세요.');
		if (!next) return;

		$.ajax({
			type: 'POST',
			url: '/playlist/rename',
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			data: { id, title: next.trim() },
		})
			.done(() => location.reload())
			.fail(() => alert('제목 변경 실패'));
	});

	// 삭제
	$(document).on('click', '.js-delete', function() {
		const id = getIdFromButton(this);
		if (!confirm('이 플레이리스트를 삭제할까요?')) return;

		$.ajax({
			type: 'POST', // (서버가 DELETE 미지원 시 POST로 처리)
			url: '/playlist/delete',
			contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
			data: { id },
		})
			.done(() => location.reload())
			.fail(() => alert('삭제 실패'));
	});
});
