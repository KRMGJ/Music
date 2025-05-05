function toggleMenu(button) {
    const menu = button.nextElementSibling;
    const isVisible = menu.style.display === 'block';
    
    document.querySelectorAll('.dropdown-menu').forEach(m => m.style.display = 'none');
    menu.style.display = isVisible ? 'none' : 'block';
}

function shareVideo(videoId) {
    const url = `https://www.youtube.com/watch?v=${videoId}`;
    navigator.clipboard.writeText(url).then(() => {
        alert("공유 링크가 복사되었습니다:\n" + url);
    });
}

// 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', function(e) {
    if (!e.target.closest('.options-wrapper')) {
        document.querySelectorAll('.dropdown-menu').forEach(m => m.style.display = 'none');
    }
});