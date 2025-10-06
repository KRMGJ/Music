$(function() {
	var $box = $('#videoDesc');
	var $btn = $('#btnToggleDesc');
	if (!$box.length || !$btn.length) return;

	function update() {
		$btn.text($box.hasClass('yt-desc--collapsed') ? '더보기' : '접기');
	}
	update();

	$(document).on('click', '#btnToggleDesc', function() {
		$box.toggleClass('yt-desc--collapsed');
		update();
	});
});

$(document).on('click', '#btnShare', function() {
	const $btn = $(this);
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
		const temp = $('<input>');
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
	const videoId = $(this).data('video');
	$('#modalVideoId').val(videoId);
	const modal = new bootstrap.Modal(document.getElementById('playlistModal'));
	modal.show();
});

// 추천 영상 더보기
$(document).on('click', '#btnMore', function() {
	const $btn = $(this);
	const token = $btn.data('token') || '';
	let url = location.pathname + '/related?size=12';
	if (token) url += '&pageToken=' + token;

	$btn.prop('disabled', true).text('불러오는 중…');

	$.getJSON(url)
		.done(function(resp) {
			const items = resp.items || [];
			if (items.length === 0) { $btn.hide(); return; }

			for (let i = 0; i < items.length; i++) {
				const r = items[i];
				const html = `
					<a class="rel-item text-decoration-none text-reset" href="/video/${r.id}">
						<div class="rel-thumb">
							<img src="${r.thumbnail || ''}" alt="${r.title || ''}"/>
							${r.formattedDuration ? `<span class="rel-dur">${r.formattedDuration}</span>` : ''}
						</div>
						<div>
							<div style="font-weight:600;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden;">
								${r.title || ''}
							</div>
							<div class="rel-meta">${r.channelTitle || ''}</div>
							<div class="rel-meta">조회수 ${r.formattedViewCount || ''} · ${r.publishedDate || ''}</div>
						</div>
					</a>`;
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

/* === 추가: 설명 렌더링 & 타임스탬프 구간이동 ================================== */
(function($) {
	'use strict';

	// HTML escape
	function esc(s) {
		return String(s).replace(/[&<>"']/g, function(m) {
			return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[m];
		});
	}

	// http/https 링크 자동 링크화
	function linkify(s) {
		var url = /\bhttps?:\/\/[^\s<]+/g;
		return s.replace(url, function(m) {
			return '<a href="' + m + '" target="_blank" rel="nofollow ugc noopener noreferrer">' + m + '</a>';
		});
	}

	// #해시태그 링크화 (내부 검색으로 연결 예시)
	function hashify(s) {
		var tag = /(^|\s)#([A-Za-z0-9가-힣_]{1,50})/g;
		return s.replace(tag, function(all, sp, word) {
			return sp + '<a class="yt-hashtag" href="/search?q=%23' + encodeURIComponent(word) + '">#' + word + '</a>';
		});
	}

	// hh:mm:ss 또는 mm:ss → 타임스탬프 버튼화
	function timeify(s) {
		var re = /\b(?:(\d{1,2}):)?(\d{1,2}):(\d{2})\b/g; // [hh:]mm:ss
		return s.replace(re, function(m, h, mi, se) {
			var sec = (parseInt(h || '0', 10) * 3600) + (parseInt(mi, 10) * 60) + parseInt(se, 10);
			return '<button type="button" class="yt-timestamp" data-s="' + sec + '">' + m + '</button>';
		});
	}

	// 최종 렌더: 줄바꿈 + 링크 + 해시태그 + 타임코드
	function renderDescription(raw) {
		var html = esc(raw || '');
		html = html.replace(/\r\n|\r|\n/g, '<br>');
		html = linkify(html);
		html = hashify(html);
		html = timeify(html);
		return '<div class="yt-desc__text">' + html + '</div>';
	}

	// 초기 렌더 (JSP에서 data-raw-desc 속성에 원문을 넣어두었음)
	function initDesc() {
		var $text = $('#descText');
		if (!$text.length) return;

		var raw = $text.data('raw-desc');
		if (typeof raw === 'undefined') {
			// fallback: 이미 들어있는 텍스트를 가져와서 렌더
			raw = $text.text();
		}
		$text.html(renderDescription(raw));
	}

	// 타임스탬프 클릭 → 구간 이동 (IFrame API 우선, 없으면 iframe src 폴백)
	$(document).on('click', '.yt-timestamp', function() {
		var sec = parseInt($(this).attr('data-s'), 10) || 0;

		// IFrame Player API 사용 중이면
		if (window.player && typeof window.player.seekTo === 'function') {
			window.player.seekTo(sec, true);
			if (typeof window.player.playVideo === 'function') window.player.playVideo();
			return;
		}

		// 폴백: iframe src=...&start= 갱신
		var $if = $('#player');
		if (!$if.length) $if = $('iframe[src*="youtube.com/embed"]');
		if ($if.length) {
			var src = $if.attr('src') || '';
			// 기존 start 제거
			src = src.replace(/([?&])start=\d+/g, '');
			var sep = src.indexOf('?') === -1 ? '?' : '&';
			$if.attr('src', src + sep + 'start=' + sec + '&autoplay=1');
		}
	});

	// DOM 준비되면 설명 렌더
	$(initDesc);

})(jQuery);

function callCreateRelated(videoId) {
	const title = prompt('새 플레이리스트 제목을 입력하세요.\n(비우면 자동으로 생성됩니다)', '');
	const privacy = 'private'; // 필요하면 prompt/select로 바꿔도 OK: 'public'|'unlisted'|'private'
	if (videoId == null) return;

	// 버튼 잠금 및 피드백
	const $btns = $('#btnMakeRelated, #btnMakeRelatedAside').prop('disabled', true).text('생성 중…');

	$.post('/youtube/related-to-playlist', {
		videoId: videoId,
		maxResults: 30,
		title: title || '',
		privacy: privacy
	})
		.done(res => {
			alert('연관 영상 플레이리스트 생성 완료!');
			console.log(res);
		})
		.fail(function(xhr) {
		  const msg =
		    (xhr && xhr.responseJSON && xhr.responseJSON.error) ||
		    (xhr && xhr.responseText) ||
		    '알 수 없는 오류';
		  alert('생성에 실패했습니다.');
		  console.error(msg);
		})
		.always(() => {
			$btns.prop('disabled', false).each(function() {
				const isAside = this.id === 'btnMakeRelatedAside';
				$(this).text(isAside ? '새 플레이리스트 만들기' : '🧩 관련영상으로 새 플레이리스트');
			});
		});
}

$(document).on('click', '#btnMakeRelated', function() {
	callCreateRelated($(this).data('video'));
});
$(document).on('click', '#btnMakeRelatedAside', function() {
	callCreateRelated($(this).data('video'));
});

