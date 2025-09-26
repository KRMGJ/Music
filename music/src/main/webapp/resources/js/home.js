$(document).ready(function() {
	document.querySelectorAll('.tab-btn').forEach(btn => {
		btn.addEventListener('click', () => {
			document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
			document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
			btn.classList.add('active');
			document.getElementById(btn.dataset.target).classList.add('active');

			// 최신 탭 최초 클릭 시 AJAX 로드
			if (btn.dataset.target === 'tab-latest') {
				const $panel = $('#tab-latest');
				if (!$panel.data('loaded')) {
					loadLatest();
				}
			}
			if (btn.dataset.target === 'tab-channels') {
				const $panel = $('#tab-channels');
				if (!$panel.data('loaded')) loadPopularChannels();
			}
		});
	});

	$(document).on('click', '.more-inline[data-more]', function() {
		const sel = $(this).data('more');
		const $btn = $(this);
		const $hidden = $(sel + ' .hidden-item');
		if ($hidden.length) {
			$hidden.removeClass('hidden-item');
			$btn.remove();
		}
	});

	// ---- AJAX 로더 ----
	function loadLatest() {
		const $panel = $('#tab-latest');
		const $grid = $('#latest-grid');
		if (!$grid.length) { console.error('#latest-grid not found'); return; }
		const $btn = $panel.find('.more-inline');

		const $loading = $('<div class="ajax-loading" style="padding:8px;color:#666;">로드 중…</div>');
		$panel.prepend($loading);

		$.getJSON('/api/home/latest', { region: 'KR', limit: 12 })
			.done(function(items) {
				renderVideoCards($grid, items, 6); // 처음 6개만 보임
				console.log('Latest videos loaded:', items);
				if ($grid.find('.hidden-item').length === 0) $btn.hide();
				$panel.data('loaded', true);
			})
			.fail(function(xhr) {
				$grid.append('<div class="message error">최신 영상을 불러오지 못했습니다.</div>');
			})
			.always(function() {
				$loading.remove();
			});
	}

	// 카드 렌더링 (XSS 안전하게 .text()/attr() 사용)
	function renderVideoCards($container, items, initialVisible) {
		const frag = document.createDocumentFragment();
		(items || []).forEach(function(v, i) {
			const article = document.createElement('article');
			article.className = 'video-card' + (i >= (initialVisible || 6) ? ' hidden-item' : '');

			// 썸네일
			const thumbWrap = document.createElement('div');
			thumbWrap.className = 'video-thumbnail-container';
			const a = document.createElement('a'); a.href = '/video/' + v.id;
			const img = document.createElement('img');
			img.className = 'video-thumbnail';
			img.src = v.thumbnail || '';
			img.alt = v.title || '';
			a.appendChild(img);
			if (v.formattedDuration) {
				const dur = document.createElement('span');
				dur.className = 'video-duration';
				dur.textContent = v.formattedDuration;
				a.appendChild(dur);
			}
			thumbWrap.appendChild(a);

			// 정보
			const info = document.createElement('div');
			info.className = 'video-info';

			const title = document.createElement('a');
			title.className = 'video-title';
			title.href = '/video/' + v.id;
			title.title = v.title || '';
			title.textContent = v.title || '';
			info.appendChild(title);

			const meta = document.createElement('div');
			meta.className = 'video-meta';
			meta.textContent = '조회수 ' + (v.formattedViewCount || '0회') + ' · ' + formatDate(v.publishedDate);
			info.appendChild(meta);

			const ch = document.createElement('div');
			ch.className = 'video-channel';
			if (v.channelThumbnail) {
				const cimg = document.createElement('img');
				cimg.className = 'channel-thumbnail';
				cimg.src = v.channelThumbnail;
				cimg.alt = v.channelTitle || '';
				ch.appendChild(cimg);
			}
			const chSpan = document.createElement('span');
			chSpan.title = v.channelTitle || '';
			chSpan.textContent = v.channelTitle || '';
			ch.appendChild(chSpan);
			info.appendChild(ch);

			const desc = document.createElement('div');
			desc.className = 'video-description';
			desc.title = v.description || '';
			desc.textContent = v.description || '';
			info.appendChild(desc);

			article.appendChild(thumbWrap);
			article.appendChild(info);
			frag.appendChild(article);
		});
		$container.empty()[0].appendChild(frag);
	}

	function loadPopularChannels() {
		const $panel = $('#tab-channels');
		const $row = $('#channels-row');
		if (!$row.length) return;

		const $loading = $('<div class="ajax-loading" style="padding:8px;color:#666;">로드 중…</div>');
		$panel.prepend($loading);

		$.getJSON('/api/home/channels', { region: 'KR', limit: 16 })
			.done(function(items) {
				console.log('Popular channels loaded:', items);
				renderChannelCards($row, items || []);
				$panel.data('loaded', true);
			})
			.fail(function() {
				$row.append('<div class="message error">인기 채널을 불러오지 못했습니다.</div>');
			})
			.always(function() { $loading.remove(); });
	}

	function renderChannelCards($container, items) {
		const frag = document.createDocumentFragment();
		(items || []).forEach(function(ch) {
			const a = document.createElement('a');
			a.className = 'channel-card';
			a.href = '/channel/' + (ch.channelId || '');
			a.style.cssText = 'flex:0 0 240px; background:#fff; border:1px solid #ddd; border-radius:12px; padding:12px; display:flex; gap:10px; align-items:center;';

			const img = document.createElement('img');
			img.src = ch.channelThumbnail || '';
			img.alt = ch.channelTitle || '';
			img.style.cssText = 'width:48px;height:48px;border-radius:50%;object-fit:cover;';
			a.appendChild(img);

			const box = document.createElement('div');
			box.style.minWidth = '0';

			const title = document.createElement('div');
			title.className = 'channel-title';
			title.title = ch.channelTitle || '';
			title.textContent = ch.channelTitle || '';
			title.style.cssText = 'font-weight:600; overflow:hidden; white-space:nowrap; text-overflow:ellipsis;';
			box.appendChild(title);

			const meta = document.createElement('div');
			meta.className = 'channel-meta';
			meta.style.cssText = 'font-size:12px;color:#666;';
			const subs = (ch.subscriberCount && ch.subscriberCount > 0) ? numberKR(ch.subscriberCount) + '명' : '구독자 비공개';
			meta.textContent = '구독자 ' + subs;
			box.appendChild(meta);

			a.appendChild(box);
			frag.appendChild(a);
		});
		$container.empty().append(frag);
	}

	function numberKR(n) {
		if (!n) return '0';
		if (n >= 100000000) return Math.floor(n / 100000000) + '억';
		if (n >= 10000) return Math.floor(n / 10000) + '만';
		return n.toLocaleString();
	}

	function formatDate(iso) {
		if (!iso) return '';
		// 'yyyy.MM.dd' 포맷
		try {
			const d = new Date(iso);
			const y = d.getFullYear();
			const m = ('0' + (d.getMonth() + 1)).slice(-2);
			const day = ('0' + d.getDate()).slice(-2);
			return `${y}.${m}.${day}`;
		} catch (e) { return ''; }
	}
});

// UX: 페이지 진입 시 인기 탭 활성 상태 그대로, 최신은 첫 클릭 때 로드됨
