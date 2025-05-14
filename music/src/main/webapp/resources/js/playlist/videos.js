function toggleMenu(button) {
    const $menu = $(button).next('.dropdown-menu');
    const isVisible = $menu.is(':visible');

    $('.dropdown-menu').hide(); // 다른 메뉴 닫기
    if (!isVisible) {
        $menu.show(); // 현재 메뉴만 열기
    }
}

function shareVideo(videoId) {
    const url = `https://www.youtube.com/watch?v=${videoId}`;
    navigator.clipboard.writeText(url).then(() => {
        alert("공유 링크가 복사되었습니다:\n" + url);
    });
}

// 외부 클릭 시 드롭다운 닫기
$(document).on('click', function (e) {
    if (!$(e.target).closest('.options-wrapper').length) {
        $('.dropdown-menu').hide();
    }
});
