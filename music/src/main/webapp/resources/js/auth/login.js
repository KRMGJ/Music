$(document).ready(function () {
	const $signUpForm = $('#signUpForm');
	const $username = $('#nickname');
	const $signUpEmail = $('#signUpEmail');
	const $signUpPassword = $('#signUpPassword');

	const $loginForm = $('#loginForm');
	const $loginEmail = $('#loginEmail');
	const $loginPassword = $('#loginPassword');

	const $signUpButton = $('#signUp');
	const $signInButton = $('#signIn');
	const $container = $('#auth-container');

	$signUpButton.on('click', function () {
		$container.addClass("right-panel-active");
	});

	$signInButton.on('click', function () {
		$container.removeClass("right-panel-active");
	});

	// 회원가입 처리
	$signUpForm.on('submit', function (e) {
		e.preventDefault();
		if (checkInputs()) {
			const nickname = $username.val().trim();
			const email = $signUpEmail.val().trim();
			const password = $signUpPassword.val().trim();

			$.ajax({
				type: "POST",
				url: "/auth/signUp",
				data: {
					nickname: nickname,
					email: email,
					password: password
				},
				success: function () {
					alert("회원가입 성공");
					window.location.href = "/auth/login";
				},
				error: function () {
					alert("회원가입 실패");
				}
			});
		}
	});

	// 로그인 처리
	$loginForm.on('submit', function (e) {
		e.preventDefault();
		if (checkLoginInputs()) {
			const email = $loginEmail.val().trim();
			const password = $loginPassword.val().trim();

			$.ajax({
				type: "POST",
				url: "/auth/login",
				data: {
					email: email,
					password: password
				},
				success: function () {
					alert("로그인 성공");
					window.location.href = "/";
				},
				error: function () {
					alert("로그인 실패");
				}
			});
		}
	});

	// 회원가입 유효성 검사
	function checkInputs() {
		let isValid = true;

		const usernameValue = $username.val().trim();
		const emailValue = $signUpEmail.val().trim();
		const passwordValue = $signUpPassword.val().trim();

		if (usernameValue === '') {
			setErrorFor($username, '이름을 입력하세요.');
			isValid = false;
		} else {
			setSuccessFor($username);
		}

		if (emailValue === '') {
			setErrorFor($signUpEmail, '이메일을 입력하세요.');
			isValid = false;
		} else if (!isEmail(emailValue)) {
			setErrorFor($signUpEmail, '이메일 형식이 아닙니다.');
			isValid = false;
		} else {
			setSuccessFor($signUpEmail);
		}

		if (passwordValue === '') {
			setErrorFor($signUpPassword, '비밀번호를 입력하세요.');
			isValid = false;
		} else {
			setSuccessFor($signUpPassword);
		}

		return isValid;
	}

	// 로그인 유효성 검사
	function checkLoginInputs() {
		let isValid = true;

		const emailValue = $loginEmail.val().trim();
		const passwordValue = $loginPassword.val().trim();

		if (emailValue === '') {
			setErrorFor($loginEmail, '이메일을 입력하세요.');
			isValid = false;
		} else if (!isEmail(emailValue)) {
			setErrorFor($loginEmail, '이메일 형식이 아닙니다.');
			isValid = false;
		} else {
			setSuccessFor($loginEmail);
		}

		if (passwordValue === '') {
			setErrorFor($loginPassword, '비밀번호를 입력하세요.');
			isValid = false;
		} else {
			setSuccessFor($loginPassword);
		}

		return isValid;
	}

	// 에러 표시
	function setErrorFor($input, message) {
		const $formControl = $input.parent();
		$formControl.removeClass('success').addClass('error');
		$formControl.find('small').text(message);
	}

	// 성공 표시
	function setSuccessFor($input) {
		const $formControl = $input.parent();
		$formControl.removeClass('error').addClass('success');
	}

	// 이메일 형식 체크
	function isEmail(email) {
		return /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
	}
});
