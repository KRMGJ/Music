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
document.addEventListener('DOMContentLoaded', function() {
	document.querySelectorAll('.video-thumbnail').forEach(thumbnail => {
		thumbnail.addEventListener('click', function(event) {
			let current = event.target;
			while (current && !current.dataset.videoId) {
				current = current.parentElement;
			}

			const videoId = current && current.dataset && current.dataset.videoId ? current.dataset.videoId.trim() : null;
			if (!videoId) {
				console.warn('Invalid videoId');
				return;
			}

			openPlayer(videoId);
		});
	});
});

// search.js (AJAX 요청 처리)
document.querySelectorAll('input[name="upload"], input[name="duration"], input[name="sort"]').forEach(input => {
	input.addEventListener('change', function() {
		// 필터 상태 가져오기
		const query = document.querySelector('input[name="query"]').value;
		const sort = document.querySelector('input[name="sort"]:checked').value;
		const duration = document.querySelector('input[name="duration"]:checked')?.value;
		const upload = document.querySelector('input[name="upload"]:checked')?.value;

		// AJAX 요청 보내기
		fetch(`/video/search?query=${query}&sort=${sort}&duration=${duration}&upload=${upload}&page=1`, {
			method: 'GET',
		}).then(response => response.text()).then(data => {
			// 검색 결과만 갱신
			document.querySelector('#searchResults').innerHTML = data;
		}).catch(error => {
			console.error('Error fetching search results:', error);
		});
	});
});

