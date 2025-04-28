document.addEventListener('DOMContentLoaded', function() {
    const logoutButton = document.getElementById("logoutButton");
    if (logoutButton) {
        logoutButton.addEventListener("click", function(event) {
            fetch("/auth/logout", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                }
            })
            .then(response => {
                if (response.ok) {
                    alert("로그아웃 성공");
                    window.location.href = "/";
                } else {
                    alert("로그아웃 실패");
                }
            })
            .catch(error => {
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
});
