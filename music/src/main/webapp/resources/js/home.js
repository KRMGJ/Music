(function() {
	const ENDPOINTS = {
		popular: '/api/home/popular',
		latest: '/api/home/latest',
		channels: '/api/home/channels'
	};
	const DEFAULT_REGION = 'KR';

	// 초기 진입: 인기 영상 로드
	$(function() {
		bindTabs();
		bindMoreButtons();

		const $popularPanel = $('#tab-popular');
		if (!$popularPanel.data('loaded')) loadPopularVideos();
	});

	/* ---------------- Tabs ---------------- */
	function bindTabs() {
		document.querySelectorAll('.tab-btn').forEach(btn => {
			btn.addEventListener('click', () => {
				document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
				document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
				btn.classList.add('active');
				document.getElementById(btn.dataset.target).classList.add('active');

				if (btn.dataset.target === 'tab-latest') {
					const $panel = $('#tab-latest');
					if (!$panel.data('loaded')) loadLatestVideos();
				}
				if (btn.dataset.target === 'tab-channels') {
					const $panel = $('#tab-channels');
					if (!$panel.data('loaded')) loadPopularChannels();
				}
			});
		});
	}

	/* --------------- More buttons --------------- */
	function bindMoreButtons() {
		$(document).on('click', '.more-inline[data-more]', function() {
			const sel = $(this).data('more');
			const $btn = $(this);
			const $hidden = $(sel + ' .hidden-item');
			if ($hidden.length) {
				$hidden.removeClass('hidden-item');
				$btn.remove();
			}
		});
	}

	/* --------------- Loaders --------------- */
	function loadPopularVideos() {
		const $panel = $('#tab-popular');
		const $grid = $('#popular-grid');
		if (!$grid.length) return;

		const $loading = $('<div class="ajax-loading">로드 중…</div>');
		$panel.prepend($loading);

		$.getJSON(ENDPOINTS.popular, { region: DEFAULT_REGION, limit: 12 })
			.done(items => {
				renderVideoCards($grid, items || [], 6);
				if ($grid.find('.hidden-item').length === 0) $panel.find('.more-inline').hide();
				$panel.data('loaded', true);
			})
			.fail(() => $grid.append('<div class="message error">인기 영상을 불러오지 못했습니다.</div>'))
			.always(() => $loading.remove());
	}

	function loadLatestVideos() {
		const $panel = $('#tab-latest');
		const $grid = $('#latest-grid');
		if (!$grid.length) return;

		const $loading = $('<div class="ajax-loading">로드 중…</div>');
		$panel.prepend($loading);

		$.getJSON(ENDPOINTS.latest, { region: DEFAULT_REGION, limit: 12 })
			.done(items => {
				renderVideoCards($grid, items || [], 6);
				if ($grid.find('.hidden-item').length === 0) $panel.find('.more-inline').hide();
				$panel.data('loaded', true);
			})
			.fail(() => $grid.append('<div class="message error">최신 영상을 불러오지 못했습니다.</div>'))
			.always(() => $loading.remove());
	}

	function loadPopularChannels() {
		const $panel = $('#tab-channels');
		const $grid = $('#channels-grid');
		if (!$grid.length) return;

		const $loading = $('<div class="ajax-loading">로드 중…</div>');
		$panel.prepend($loading);

		$.getJSON(ENDPOINTS.channels, { region: DEFAULT_REGION, limit: 24 })
			.done(items => {
				renderChannelCardsGrid($grid, items || [], 12); // 12개 우선
				if ($grid.find('.hidden-item').length === 0) $panel.find('.more-inline').hide();
				$panel.data('loaded', true);
			})
			.fail(() => $grid.append('<div class="message error">인기 채널을 불러오지 못했습니다.</div>'))
			.always(() => $loading.remove());
	}

	/* --------------- Renderers (template 기반) --------------- */
	function renderVideoCards($container, items, initialVisible) {
		if (!$container || !$container.length) return;
		const tpl = document.getElementById('tpl-video-card');
		if (!tpl) return;

		const frag = document.createDocumentFragment();
		(items || []).forEach((v, i) => {
			const node = tpl.content.firstElementChild.cloneNode(true);

			// 썸네일/링크
			const a = node.querySelector('.video-link');
			a.href = '/video/' + (v.id || '');

			const img = node.querySelector('.video-thumbnail');
			img.src = v.thumbnail || '';
			img.alt = v.title || '';

			const dur = node.querySelector('.video-duration');
			if (v.formattedDuration) { dur.textContent = v.formattedDuration; }
			else { dur.remove(); }

			// 텍스트
			const title = node.querySelector('.video-title');
			title.href = '/video/' + (v.id || '');
			title.textContent = v.title || '';
			title.title = v.title || '';

			const meta = node.querySelector('.video-meta');
			meta.textContent = '조회수 ' + (v.formattedViewCount || '0회') + ' · ' + formatDate(v.publishedDate);

			const chImg = node.querySelector('.channel-thumbnail');
			if (v.channelThumbnail) {
				chImg.src = v.channelThumbnail; chImg.alt = v.channelTitle || '';
			} else {
				chImg.remove();
			}

			const chSpan = node.querySelector('.video-channel span');
			chSpan.textContent = v.channelTitle || '';
			chSpan.title = v.channelTitle || '';

			const desc = node.querySelector('.video-description');
			desc.textContent = v.description || '';
			desc.title = v.description || '';

			if (i >= (initialVisible || 6)) node.classList.add('hidden-item');
			frag.appendChild(node);
		});

		$container.empty().append(frag);
	}

	function renderChannelCardsGrid($container, items, initialVisible) {
		if (!$container || !$container.length) return;
		const tpl = document.getElementById('tpl-channel-card');
		if (!tpl) return;

		const frag = document.createDocumentFragment();
		(items || []).forEach((c, i) => {
			const node = tpl.content.firstElementChild.cloneNode(true);

			node.querySelector('.rank-badge').textContent = (i + 1);

			const img = node.querySelector('.channel-avatar');
			img.src = c.channelThumbnail || '';
			img.alt = c.channelTitle || '';

			const title = node.querySelector('.channel-title');
			title.href = '/channel/' + (c.channelId || '');
			title.textContent = c.channelTitle || '';
			title.title = c.channelTitle || '';

			const subs = (c.subscriberCount && c.subscriberCount > 0) ? numberKR(c.subscriberCount) + '명' : '구독자 비공개';
			const vids = (c.videoCount && c.videoCount > 0) ? ' · 동영상 ' + c.videoCount.toLocaleString() + '개' : '';
			node.querySelector('.channel-meta').textContent = '구독자 ' + subs + vids;

			if (i >= (initialVisible || 12)) node.classList.add('hidden-item');
			frag.appendChild(node);
		});

		$container.empty().append(frag);
	}

	/* --------------- Utils --------------- */
	function formatDate(iso) {
		if (!iso) return '';
		try {
			const d = new Date(iso);
			const y = d.getFullYear();
			const m = ('0' + (d.getMonth() + 1)).slice(-2);
			const day = ('0' + d.getDate()).slice(-2);
			return `${y}.${m}.${day}`;
		} catch (e) { return ''; }
	}

	function numberKR(n) {
		if (!n) return '0';
		if (n >= 100000000) return Math.floor(n / 100000000) + '억';
		if (n >= 10000) return Math.floor(n / 10000) + '만';
		return n.toLocaleString();
	}
})();
