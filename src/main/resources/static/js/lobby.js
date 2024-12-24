const lobbyInfo = document.getElementById("lobbyInfo");

const resizeObserver = new ResizeObserver(entries => {
    for (let entry of entries) {
        if (entry.contentRect.width < 500) {
            document.getElementById("topnav-icon").style.display = "block";
            document.getElementById("topnav-label").style.display = "none";
        } else {
            document.getElementById("topnav-icon").style.display = "none";
            document.getElementById("topnav-label").style.display = "block";
        }
    }
});

resizeObserver.observe(lobbyInfo);

const inviteModal = document.getElementById("inviteModal");

function openInviteModal() {
    inviteModal.style.display = "block";
}

function closeInviteModal() {
    inviteModal.style.display = "none";
}

function writeInviteModalLink() {
    const inviteLink = document.getElementById("inviteModalLink");
    inviteLink.value = window.location.href;
}

function checkCloseInviteModal(event) {
    if(event.target === inviteModal) {
        closeInviteModal();
    }
}

const gameEndModal = document.getElementById("gameEndModal");

function openGameEndModal() {
    gameEndModal.style.display = "block";
}

function closeGameEndModal() {
    gameEndModal.style.display = "none";
}

function checkCloseGameEndModal(event) {
    if(event.target === gameEndModal) {
        closeGameEndModal();
    }
}

function copyInviteLink() {
    const inviteLink = document.getElementById("inviteModalLink");
    inviteLink.select();
    inviteLink.setSelectionRange(0, 99999);
    document.execCommand("copy");
}

window.onclick = function(event) {
    checkCloseInviteModal(event);
    checkCloseGameEndModal(event)
}