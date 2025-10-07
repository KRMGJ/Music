function renderCommentItem(cmt) {
	var replyCount = cmt.totalReplyCount || (cmt.replies ? cmt.replies.length : 0);

	var html = ''
		+ '<div class="comment-item">'
		+ '  <img class="comment-avatar" src="' + (cmt.authorProfileImageUrl || '/resources/images/default-avatar.png') + '" alt="avatar"/>'
		+ '  <div class="comment-body">'
		+ '    <div class="comment-author">' + (cmt.authorDisplayName || '알 수 없음') + '</div>'
		+ '    <div class="comment-text">' + (cmt.textDisplay || '') + '</div>'
		+ '    <button type="button" class="comment-more" style="display:none;">더보기</button>'
		+ '    <div class="comment-meta">' + formatRelativeKST(cmt.publishedAt) + ' · 좋아요 ' + (cmt.likeCount || 0) + '</div>';

	if (replyCount > 0) {
		html += ''
			+ '    <div class="reply-toggle-line">'
			+ '      <button type="button" class="btn-reply-toggle" '
			+ '              data-expanded="false" data-count="' + replyCount + '">'
			+ '        답글 ' + replyCount + '개 보기'
			+ '      </button>'
			+ '    </div>'
			+ '    <div class="reply-list" style="display:none;">';

		if (cmt.replies && cmt.replies.length > 0) {
			for (var j = 0; j < cmt.replies.length; j++) {
				var r = cmt.replies[j];
				html += ''
					+ '<div class="reply-item">'
					+ '  <img class="comment-avatar" src="' + (r.authorProfileImageUrl || '/resources/images/default-avatar.png') + '" alt="avatar"/>'
					+ '  <div class="comment-body">'
					+ '    <div class="comment-author">' + (r.authorDisplayName || '알 수 없음') + '</div>'
					+ '    <div class="comment-text">' + (r.textDisplay || '') + '</div>'
					+ '    <div class="comment-meta">' + formatRelativeKST(cmt.publishedAt) + ' · 좋아요 ' + (r.likeCount || 0) + '</div>'
					+ '  </div>'
					+ '</div>';
			}
		}
		html += '    </div>';
	}

	html += '  </div></div>';
	return html;
}


function loadComments(token, order) {
	const videoId = $('#comments').data('video');
	const params = [];
	params.push('size=20');
	if (order) params.push('order=' + encodeURIComponent(order));
	if (token) params.push('pageToken=' + encodeURIComponent(token));

	const query = params.length ? `?${params.join('&')}` : '';
	const url = `/youtube/${encodeURIComponent(videoId)}/comments${query}`;

	const $btn = $('#btnMoreComments');
	$btn.prop('disabled', true).text('불러오는 중…');

	$.getJSON(url)
		.done(function(resp) {
			if (resp.errorMessage) {
				$('#commentList').html(
					`<div class="text-muted" style="padding:12px 0;">${resp.errorMessage}</div>`
				);
				$('#btnMoreComments').hide();
				return;
			}

			const items = resp.items || [];
			for (const item of items) {
				$('#commentList').append(renderCommentItem(item));
			}
			applyClamp($('#commentList'));

			if (resp.nextPageToken) {
				$btn
					.data('token', resp.nextPageToken)
					.prop('disabled', false)
					.text('댓글 더보기')
					.removeClass('d-none')
					.show();
			} else {
				$btn.addClass('d-none').hide();
			}
		})
		.fail(function() {
			$btn.prop('disabled', false).text('다시 시도');
		});
}

$(document).on('click', '.btn-reply-toggle', function() {
	var $btn = $(this);
	var $wrap = $btn.closest('.comment-item').find('.reply-list').first();
	var expanded = $btn.attr('data-expanded') === 'true';
	var count = $btn.data('count');

	if (expanded) {
		$wrap.slideUp(150);
		$btn.attr('data-expanded', 'false').text('답글 ' + count + '개 보기');
	} else {
		$wrap.slideDown(150);
		$btn.attr('data-expanded', 'true').text('답글 ' + count + '개 숨기기');
	}
});

// 댓글 등록
$(document).on('click', '#btnSubmitComment', function() {
	const videoId = $('#comments').data('video');
	const channelId = $('#comments').data('channel');
	const text = ($('#commentText').val() || '').trim();

	$('#commentError').hide().text('');
	if (!text) {
		$('#commentError').text('댓글 내용을 입력하세요.').show();
		return;
	}

	$('#btnSubmitComment').prop('disabled', true);
	$('#commentSaving').show();

	$.ajax({
		url: '/youtube/' + encodeURIComponent(videoId) + '/comments',
		method: 'POST',
		contentType: 'application/json',
		data: JSON.stringify({ videoId, channelId, text })
	})
		.done(function() {
			$('#commentText').val('');
			$('#commentList').empty();
			$('#btnMoreComments').data('token', '').removeClass('d-none');
			const order = $('#commentOrder').val();
			loadComments('', order);
		})
		.fail(function(xhr) {
			let msg = '등록 실패';
			try {
				const body = JSON.parse(xhr.responseText);
				if (body.message) msg = body.message + (body.reason ? ` (${body.reason})` : '');
			} catch (_) { }
			$('#commentError').text(msg).show();
		})
		.always(function() {
			$('#btnSubmitComment').prop('disabled', false);
			$('#commentSaving').hide();
		});
});

// 최초 로딩
$(function() {
	if ($('#comments').length) {
		const order = $('#commentOrder').val();
		loadComments('', order);
	}
});

// 정렬 변경 시 다시 로딩
$(document).on('change', '#commentOrder', function() {
	$('#commentList').empty();
	$('#btnMoreComments').data('token', '').removeClass('d-none');
	loadComments('', $(this).val());
});

// 더보기
$(document).on('click', '#btnMoreComments', function() {
	const token = $(this).data('token') || '';
	const order = $('#commentOrder').val();
	loadComments(token, order);
});

function formatDateKST(iso) {
	if (!iso) return '';
	const d = new Date(iso);
	if (isNaN(d.getTime())) return iso;

	const y = new Intl.DateTimeFormat('ko-KR', { timeZone: 'Asia/Seoul', year: 'numeric' }).format(d);
	const m = new Intl.DateTimeFormat('ko-KR', { timeZone: 'Asia/Seoul', month: '2-digit' }).format(d);
	const day = new Intl.DateTimeFormat('ko-KR', { timeZone: 'Asia/Seoul', day: '2-digit' }).format(d);
	return `${y}-${m}-${day}`;
}

// 상대 시각(유튜브 느낌) - 몇 분/시간 전, 어제, 며칠 전...
function formatRelativeKST(iso) {
	if (!iso) return '';
	const d = new Date(iso);
	const diffSec = (Date.now() - d.getTime()) / 1000;

	if (diffSec < 60) return '방금 전';
	if (diffSec < 3600) return `${Math.floor(diffSec / 60)}분 전`;
	if (diffSec < 86400) return `${Math.floor(diffSec / 3600)}시간 전`;

	const days = Math.floor(diffSec / 86400);
	if (days === 1) return '어제';
	if (days < 7) return `${days}일 전`;

	return formatDateKST(iso); // 1주 이상은 절대시간으로
}

// 요소가 잘리는지 판단
function isOverflow($el) {
	const el = $el[0];
	if (!el) return false;
	return el.scrollHeight - 1 > el.clientHeight; // 여유 1px
}

// 새로 들어온 댓글에 3줄 클램프 적용
function applyClamp($scope) {
	$scope.find('.comment-text').each(function() {
		const $t = $(this);
		if ($t.data('clamp-inited')) return;

		// 일단 3줄로 제한
		$t.addClass('clamp-3');
		// 그라데이션 & 버튼 표시 여부 결정
		if (isOverflow($t)) {
			$t.addClass('has-gradient');
			$t.next('.comment-more').show();
		} else {
			$t.removeClass('clamp-3 has-gradient');
			$t.next('.comment-more').hide();
		}
		$t.data('clamp-inited', true);
	});
}

// 더보기/간략히 토글
$(document).on('click', '.comment-more', function() {
	const $btn = $(this);
	const $text = $btn.prev('.comment-text');
	const collapsed = $text.hasClass('clamp-3');

	if (collapsed) {
		$text.removeClass('clamp-3 has-gradient');
		$btn.text('간략히');
	} else {
		$text.addClass('clamp-3 has-gradient');
		$btn.text('더보기');
	}
});

