const selectedRadios = {};

$(document).ready(function () {
	// 페이지 로드 시 현재 선택된 라디오 저장
	$('input[type=radio]:checked').each(function () {
		selectedRadios[this.name] = this;
	});

	// 라디오 버튼 클릭 시 toggle 동작 설정
	$('input[type=radio]').on('click', function () {
		const name = this.name;

		if (selectedRadios[name] === this) {
			$(this).prop('checked', false);
			selectedRadios[name] = null;
		} else {
			selectedRadios[name] = this;
		}
	});

	// 로그아웃 처리
	const $logoutButton = $('#logoutButton');
	if ($logoutButton.length) {
		$logoutButton.on('click', function () {
			$.ajax({
				type: "POST",
				url: "/auth/logout",
				contentType: "application/x-www-form-urlencoded",
				success: function () {
					alert("로그아웃 성공");
					window.location.href = "/";
				},
				error: function () {
					alert("로그아웃 실패");
				}
			});
		});
	}

	// 스크롤 시 헤더 스타일 변경
	const $header = $('header');
	if ($header.length) {
		$(window).on('scroll', function () {
			if ($(window).scrollTop() > 50) {
				$header.addClass('scrolled');
			} else {
				$header.removeClass('scrolled');
			}
		});
	}

	// 필터 모달 열고 닫기
	$('#filterBtn').on('click', function () {
		$('#filterModal').addClass('show');
	});

	$('#closeModal').on('click', function () {
		$('#filterModal').removeClass('show');
	});
});
