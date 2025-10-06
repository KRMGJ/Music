$(document).ready(function() {
	const $cards = $(".playlist-card");

	// 정렬
	$("#sortOption").on("change", function() {
		const sort = $(this).val();
		const sorted = $cards.sort((a, b) => {
			const $a = $(a), $b = $(b);
			if (sort === "latest") {
				return $b.data("created") - $a.data("created");
			} else if (sort === "oldest") {
				return $a.data("created") - $b.data("created");
			} else if (sort === "title") {
				return $a.data("title").localeCompare($b.data("title"));
			} else if (sort === "count") {
				return $b.data("count") - $a.data("count");
			}
			return 0;
		});
		$(".playlist-grid").html(sorted);
	});

	// 필터
	$("#filterOption").on("change", function() {
		const val = $(this).val();
		$cards.show().filter(function() {
			return val !== "all" && $(this).data("privacy") !== val;
		}).hide();
	});

	// 검색
	$("#searchInput").on("input", function() {
		const keyword = $(this).val().toLowerCase();
		$cards.show().filter(function() {
			return !$(this).data("title").toLowerCase().includes(keyword);
		}).hide();
	});
});
