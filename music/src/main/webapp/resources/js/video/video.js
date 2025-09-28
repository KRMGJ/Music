$(document).on('click', '#btnToggleDesc', function() {
	var $desc = $('#descText');
	$desc.toggleClass('expanded');
	if ($desc.hasClass('expanded')) {
		$(this).text('접기');
	} else {
		$(this).text('더보기');
	}
});

$(document).on('click', '#btnShare', function() {
	var $btn = $(this);
	if (navigator.clipboard && navigator.clipboard.writeText) {
		navigator.clipboard.writeText(location.href)
			.then(function() {
				$btn.text('복사됨!');
				setTimeout(function() { $btn.text('🔗 공유'); }, 1200);
			})
			.catch(function() {
				alert('복사 실패. 주소창에서 직접 복사해 주세요.');
			});
	} else {
		// fallback: execCommand (구형 브라우저 호환)
		var temp = $('<input>');
		$('body').append(temp);
		temp.val(location.href).select();
		try {
			document.execCommand('copy');
			$btn.text('복사됨!');
			setTimeout(function() { $btn.text('🔗 공유'); }, 1200);
		} catch (e) {
			alert('복사 실패. 주소창에서 직접 복사해 주세요.');
		}
		temp.remove();
	}
});

// 플레이리스트 모달 열기
$(document).on('click', '#btnSave', function() {
	var videoId = $(this).data('video');
	$('#modalVideoId').val(videoId);
	var modal = new bootstrap.Modal(document.getElementById('playlistModal'));
	modal.show();
});

// 추천 영상 더보기
$(document).on('click', '#btnMore', function() {
	var $btn = $(this);
	var token = $btn.data('token') || '';
	var url = location.pathname + '/related?size=12';
	if (token) url += '&pageToken=' + token;

	$btn.prop('disabled', true).text('불러오는 중…');

	$.getJSON(url)
		.done(function(resp) {
			var items = resp.items || [];
			if (items.length === 0) { $btn.hide(); return; }

			for (var i = 0; i < items.length; i++) {
				var r = items[i];
				var html = ''
					+ '<a class="rel-item text-decoration-none text-reset" href="/video/' + r.id + '">'
					+ '  <div class="rel-thumb"><img src="' + (r.thumbnail || '') + '" alt="' + (r.title || '') + '"/>';
				if (r.formattedDuration) html += '<span class="rel-dur">' + r.formattedDuration + '</span>';
				html += '  </div>'
					+ '  <div>'
					+ '    <div style="font-weight:600;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;">'
					+ (r.title || '') + '</div>'
					+ '    <div class="rel-meta">' + (r.channelTitle || '') + '</div>'
					+ '    <div class="rel-meta">조회수 ' + (r.formattedViewCount || '') + ' · ' + (r.publishedDate || '') + '</div>'
					+ '  </div>'
					+ '</a>';
				$('#relList').append(html);
			}

			if (resp.nextPageToken) {
				$btn.data('token', resp.nextPageToken).prop('disabled', false).text('더보기');
			} else {
				$btn.hide();
			}
		})
		.fail(function() {
			$btn.prop('disabled', false).text('다시 시도');
		});
});
