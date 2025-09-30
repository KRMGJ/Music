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
		var $card = $(this).closest('.playlist-card');
		var id = $card.data('pl-id');
		var $titleDiv = $card.find('.playlist-title');
		var currentText = $.trim($titleDiv.text());

		// 이미 편집 중이면 중복 실행 방지
		if ($titleDiv.find('input').length) return;

		// input + 버튼 추가
		var $input = $('<input type="text" class="rename-input"/>')
			.val(currentText);
		var $saveBtn = $('<button class="btn primary btn-sm">저장</button>');
		var $cancelBtn = $('<button class="btn ghost btn-sm">취소</button>');

		// 기존 내용 지우고 편집 UI 삽입
		$titleDiv.empty().append($input).append($saveBtn).append($cancelBtn);
		$input.focus().select();

		// 저장 실행
		function doSave() {
			var next = $.trim($input.val());
			if (!next || next === currentText) {
				cancelEdit();
				return;
			}
			$.ajax({
				type: 'POST',
				url: '/playlist/rename',
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				data: { id: id, title: next }
			})
				.done(function() { location.reload(); })
				.fail(function() { alert('제목 변경 실패'); cancelEdit(); });
		}

		// 취소 실행
		function cancelEdit() {
			$titleDiv.empty().append(
				$('<a/>').attr('href', '/playlist/' + id).addClass('title-link').text(currentText)
			);
		}

		$saveBtn.on('click', doSave);
		$cancelBtn.on('click', cancelEdit);
		$input.on('keydown', function(e) {
			if (e.key === 'Enter') doSave();
			if (e.key === 'Escape') cancelEdit();
		});
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
