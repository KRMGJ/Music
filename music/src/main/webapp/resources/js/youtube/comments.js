function renderCommentItem(cmt) {
	var html = ''
		+ '<div class="comment-item">'
		+ '  <img class="comment-avatar" src="' + (cmt.authorProfileImageUrl || '') + '" alt="avatar"/>'
		+ '  <div class="comment-body">'
		+ '    <div class="comment-author">' + (cmt.authorDisplayName || '알 수 없음') + '</div>'
		+ '    <div class="comment-text">' + (cmt.textDisplay || '') + '</div>'
		+ '    <div class="comment-meta">' + (cmt.publishedAt || '') + ' · 좋아요 ' + (cmt.likeCount || 0) + '</div>';

	// 대댓글이 있으면 간단히 표시
	if (cmt.replies && cmt.replies.length > 0) {
		html += '<div class="reply-list">';
		for (var j = 0; j < cmt.replies.length; j++) {
			var r = cmt.replies[j];
			html += ''
				+ '<div class="reply-item">'
				+ '  <img class="comment-avatar" src="' + (r.authorProfileImageUrl || '') + '" alt="avatar"/>'
				+ '  <div class="comment-body">'
				+ '    <div class="comment-author">' + (r.authorDisplayName || '알 수 없음') + '</div>'
				+ '    <div class="comment-text">' + (r.textDisplay || '') + '</div>'
				+ '    <div class="comment-meta">' + (r.publishedAt || '') + ' · 좋아요 ' + (r.likeCount || 0) + '</div>'
				+ '  </div>'
				+ '</div>';
		}
		html += '</div>';
	}

	html += '  </div></div>';
	return html;
}

function loadComments(token, order) {
	var videoId = $('#comments').data('video');
	var params = [];
	params.push('size=20');
	if (order) params.push('order=' + encodeURIComponent(order));
	if (token) params.push('pageToken=' + encodeURIComponent(token));

	var url = '/youtube/' + encodeURIComponent(videoId) + '/comments' + (params.length ? ('?' + params.join('&')) : '');

	var $btn = $('#btnMoreComments');
	$btn.prop('disabled', true).text('불러오는 중…');

	$.getJSON(url)
		.done(function(resp) {
			if (resp.errorMessage) {
				$('#commentList').html(
					'<div class="text-muted" style="padding:12px 0;">' +
					resp.errorMessage +
					'</div>'
				);
				$('#btnMoreComments').hide();
				return;
			}
			var items = resp.items || [];
			for (var i = 0; i < items.length; i++) {
				$('#commentList').append(renderCommentItem(items[i]));
			}

			if (resp.nextPageToken) {
				$btn.data('token', resp.nextPageToken).prop('disabled', false).text('댓글 더보기').removeClass('d-none').show();
			} else {
				$btn.addClass('d-none').hide();
			}
		})
		.fail(function() {
			$btn.prop('disabled', false).text('다시 시도');
		});
}
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
				if (body.message) msg = body.message;
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
		var order = $('#commentOrder').val();
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
	var token = $(this).data('token') || '';
	var order = $('#commentOrder').val();
	loadComments(token, order);
});
