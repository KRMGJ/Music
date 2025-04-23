function openPlaylistModal(videoId) {
    document.getElementById('modalVideoId').value = videoId;
    const modal = new bootstrap.Modal(document.getElementById('playlistModal'));
    modal.show();
}

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
