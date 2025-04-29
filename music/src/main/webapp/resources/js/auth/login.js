document.addEventListener("DOMContentLoaded", function() {
	const signUpForm = document.getElementById('signUpForm');
	const username = document.getElementById('nickname');
	const signUpEmail = document.getElementById('signUpEmail');
	const signUpPassword = document.getElementById('signUpPassword');

	const loginForm = document.getElementById('loginForm');
	const loginEmail = document.getElementById('loginEmail');
	const loginPassword = document.getElementById('loginPassword');

	const signUpButton = document.getElementById('signUp');
	const signInButton = document.getElementById('signIn');
	const container = document.getElementById('auth-container');

	signUpButton.addEventListener('click', () => {
		container.classList.add("right-panel-active");
	});

	signInButton.addEventListener('click', () => {
		container.classList.remove("right-panel-active");
	});

	// 회원가입 처리
	signUpForm.addEventListener('submit', function(e) {
		e.preventDefault();
		if (checkInputs()) {
			const nickname = username.value.trim();
			const email = signUpEmail.value.trim();
			const password = signUpPassword.value.trim();

			fetch("/user/signUp", {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded"
				},
				body: new URLSearchParams({
					nickname: nickname,
					email: email,
					password: password
				})
			}).then(response => {
				if (!response.ok) {
					return response.text().then(errorMessage => {
						alert("회원가입 실패");
					});
				}
				return response.text().then(successMessage => {
					alert("회원가입 성공");
					window.location.href = "/auth/login";
				});
			}).catch(error => {
				console.error("회원가입 중 오류 발생:", error);
				alert("회원가입 중 오류가 발생했습니다.");
			});
		}
	});

	// 로그인 처리
	loginForm.addEventListener("submit", function(event) {
		event.preventDefault();
		if (checkLoginInputs()) {
			const email = loginEmail.value.trim();
			const password = loginPassword.value.trim();

			fetch("/auth/login", {
				method: "POST",
				headers: {
					"Content-Type": "application/x-www-form-urlencoded"
				},
				body: new URLSearchParams({
					email: email,
					password: password
				})
			}).then(response => {
				if (!response.ok) {
					return response.text().then(errorMessage => {
						alert("로그인 실패");
					});
				}
				return response.text().then(successMessage => {
					alert("로그인 성공");
					window.location.href = "/";
				});
			}).catch(error => {
				console.error("로그인 중 오류 발생:", error);
				alert("로그인 중 오류가 발생했습니다.");
			});
		}
	});

	// 회원가입 유효성 검사
	function checkInputs() {
		let isValid = true;

		const usernameValue = username.value.trim();
		const emailValue = signUpEmail.value.trim();
		const passwordValue = signUpPassword.value.trim();

		if (usernameValue === '') {
			setErrorFor(username, 'Username cannot be blank');
			isValid = false;
		} else {
			setSuccessFor(username);
		}

		if (emailValue === '') {
			setErrorFor(signUpEmail, 'Email cannot be blank');
			isValid = false;
		} else if (!isEmail(emailValue)) {
			setErrorFor(signUpEmail, 'Not a valid email');
			isValid = false;
		} else {
			setSuccessFor(signUpEmail);
		}

		if (passwordValue === '') {
			setErrorFor(signUpPassword, 'Password cannot be blank');
			isValid = false;
		} else {
			setSuccessFor(signUpPassword);
		}

		return isValid;
	}

	// 로그인 유효성 검사
	function checkLoginInputs() {
		let isValid = true;

		const emailValue = loginEmail.value.trim();
		const passwordValue = loginPassword.value.trim();

		if (emailValue === '') {
			setErrorFor(loginEmail, 'Email cannot be blank');
			isValid = false;
		} else if (!isEmail(emailValue)) {
			setErrorFor(loginEmail, 'Not a valid email');
			isValid = false;
		} else {
			setSuccessFor(loginEmail);
		}

		if (passwordValue === '') {
			setErrorFor(loginPassword, 'Password cannot be blank');
			isValid = false;
		} else {
			setSuccessFor(loginPassword);
		}

		return isValid;
	}

	// 에러 표시
	function setErrorFor(input, message) {
		const formControl = input.parentElement;
		const small = formControl.querySelector('small');
		formControl.className = 'custom-form-control error';
		small.innerText = message;
	}

	// 성공 표시
	function setSuccessFor(input) {
		const formControl = input.parentElement;
		formControl.className = 'custom-form-control success';
	}

	// 이메일 형식 체크
	function isEmail(email) {
		return /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
	}
});
