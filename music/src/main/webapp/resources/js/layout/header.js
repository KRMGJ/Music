const selectedRadios = {};

function toggleRadio(radio) {
	const name = radio.name;

	// 이미 선택된 radio를 다시 클릭하면 해제
	if (selectedRadios[name] === radio) {
		radio.checked = false;
		selectedRadios[name] = null;
	} else {
		selectedRadios[name] = radio;
	}
}

document.addEventListener('DOMContentLoaded', function() {

	// 페이지 로드 시 현재 선택된 라디오 저장
	window.addEventListener('DOMContentLoaded', () => {
		document.querySelectorAll('input[type=radio]').forEach(radio => {
			if (radio.checked) {
				selectedRadios[radio.name] = radio;
			}
		});
	});

	const logoutButton = document.getElementById("logoutButton");
	console.log("Logout button:", logoutButton);
	if (logoutButton) {
		logoutButton.addEventListener("click", function() {
			fetch("/auth/logout", {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded"
				}
			}).then(response => {
				if (response.ok) {
					alert("로그아웃 성공");
					window.location.href = "/";
				} else {
					alert("로그아웃 실패");
				}
			}).catch(error => {
				console.error("로그아웃 중 오류 발생:", error);
				alert("로그아웃 중 오류가 발생했습니다.");
			});
		});
	}

	// 스크롤 시 헤더 스타일 변경
	const header = document.querySelector('header');
	if (header) {
		window.addEventListener('scroll', function() {
			if (window.scrollY > 50) {
				header.classList.add('scrolled');
			} else {
				header.classList.remove('scrolled');
			}
		});
	}

	const filterBtn = document.getElementById("filterBtn");
	const filterModal = document.getElementById("filterModal");
	const closeModal = document.getElementById("closeModal");

	filterBtn.addEventListener("click", () => {
		filterModal.classList.add("show");
	});

	closeModal.addEventListener("click", () => {
		filterModal.classList.remove("show");
	});
});
