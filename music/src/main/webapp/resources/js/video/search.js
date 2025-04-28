// 비디오 검색 관련 JavaScript
function openPlaylistModal(videoId) {
    document.getElementById('modalVideoId').value = videoId;
    const modal = new bootstrap.Modal(document.getElementById('playlistModal'));
    modal.show();
}

// 비디오 플레이어 열기
function openPlayer(videoId) {
    const playerDiv = document.getElementById('player-' + videoId);
    if (!playerDiv) return;

    if (playerDiv.innerHTML.trim() !== '') {
        playerDiv.innerHTML = '';
        return;
    }

    const iframe = document.createElement('iframe');
    iframe.src = "https://www.youtube.com/embed/" + videoId + "?autoplay=1";
    iframe.title = "YouTube video player";
    iframe.allow = "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share";
    iframe.allowFullscreen = true;
    iframe.frameBorder = "0";
    iframe.className = "w-100 h-100";

    const ratioDiv = document.createElement('div');
    ratioDiv.className = "ratio ratio-16x9";
    ratioDiv.appendChild(iframe);

    playerDiv.innerHTML = '';
    playerDiv.appendChild(ratioDiv);

    window.scrollTo({ top: playerDiv.offsetTop - 80, behavior: 'smooth' });
}

// 썸네일을 클릭하면 비디오 플레이어 열기
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.video-thumbnail').forEach(thumbnail => {
        thumbnail.addEventListener('click', function (event) {
            let current = event.target;
            while (current && !current.dataset.videoId) {
                current = current.parentElement;
            }

            const videoId = current?.dataset?.videoId?.trim();
            if (!videoId) {
                console.warn('Invalid videoId');
                return;
            }

            openPlayer(videoId);
        });
    });
});

// 플레이리스트에 비디오 추가
document.getElementById("addToPlaylistForm").addEventListener("submit", function(event) {
    event.preventDefault();  // 폼 제출을 막고 Ajax 요청을 보냄

    var videoId = document.getElementById('modalVideoId').value;
    var playlistId = document.getElementById('playlistId').value;


   fetch("/playlist/addVideo", {
       method: 'POST',
       headers: {
           'Content-Type': 'application/x-www-form-urlencoded'
       },
       body: new URLSearchParams({
           playlistId: playlistId,
           videoId: videoId
       })
   })
   .then(response => {
       if (response.ok) {
           return response.text();  // 서버에서 보낸 성공 메시지를 반환
       }
       return Promise.reject('Failed to add video');
   })
   .then(successMessage => {
       alert(successMessage);  // "Video added to playlist successfully" 메시지
   })
   .catch(error => {
       alert(error);  // "Error adding video to playlist" 메시지
   });

});
