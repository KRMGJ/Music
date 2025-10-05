(function($) {
	function escapeHtml(str) {
		return str
			.replace(/&/g, "&amp;")
			.replace(/</g, "&lt;")
			.replace(/>/g, "&gt;")
			.replace(/"/g, "&quot;")
			.replace(/'/g, "&#39;");
	}

	function linkify(html) {
		
		html = html.replace(/(https?:\/\/[^\s<]+)/g, function(m) {
			return '<a href="' + m + '" target="_blank" rel="noopener noreferrer nofollow">' + m + '</a>';
		});
		
		html = html.replace(/(^|\s)#([\w가-힣_]+)/g, function(_m, p1, tag) {
			return p1 + '<a href="/video/search?query=%23' + encodeURIComponent(tag) + '">#' + tag + '</a>';
		});
		return html;
	}

	function renderDesc() {
		const $desc = $("#descText");
		const $btn = $("#btnToggleDesc");
		if ($desc.length === 0) return;

		const raw = $desc.text() || "";
		let html = escapeHtml(raw);
		html = html.replace(/\r\n|\r|\n/g, "<br/>");
		html = linkify(html);
		$desc.html(html);

		setTimeout(function() {
			if ($btn.length === 0) return;
			const el = $desc[0];
			const needsToggle = el.scrollHeight > el.clientHeight + 1;
			$btn.toggle(needsToggle);
		}, 50);
	}

	// 더보기/접기
	$(document).on("click", "#btnToggleDesc", function() {
		const $desc = $("#descText");
		if ($desc.length === 0) return;
		$desc.toggleClass("expanded");
		$(this).text($desc.hasClass("expanded") ? "접기" : "더보기");
	});

	$(window).on("resize", function() {
		renderDesc();
	});

	$(renderDesc);
})(jQuery);