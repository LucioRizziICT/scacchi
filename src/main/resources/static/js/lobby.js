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

const menuButtons = {
    game : document.getElementById("lobbyInfoGameButton-label"),
    chat : document.getElementById("lobbyInfoChatButton-label"),
    settings : document.getElementById("lobbyInfoSettingsButton-label"),
    gameIcon : document.getElementById("lobbyInfoGameButton-icon"),
    chatIcon : document.getElementById("lobbyInfoChatButton-icon"),
    settingsIcon : document.getElementById("lobbyInfoSettingsButton-icon")
}

const menuContents = {
    game : document.getElementById("lobbyInfoContentGame"),
    chat : document.getElementById("lobbyInfoContentChat"),
    settings : document.getElementById("lobbyInfoContentSettings")
}

function selectLobbyGameMenu() {
    menuButtons.game.classList.add("button-active")
    menuButtons.chat.classList.remove("button-active")
    menuButtons.settings.classList.remove("button-active")

    menuButtons.gameIcon.classList.add("button-active")
    menuButtons.chatIcon.classList.remove("button-active")
    menuButtons.settingsIcon.classList.remove("button-active")

    menuContents.game.style.display = "block";
    menuContents.chat.style.display = "none";
    menuContents.settings.style.display = "none";
}

function selectLobbyChatMenu() {
    menuButtons.game.classList.remove("button-active")
    menuButtons.chat.classList.add("button-active")
    menuButtons.settings.classList.remove("button-active")

    menuButtons.gameIcon.classList.remove("button-active")
    menuButtons.chatIcon.classList.add("button-active")
    menuButtons.settingsIcon.classList.remove("button-active")

    menuContents.game.style.display = "none";
    menuContents.chat.style.display = "block";
    menuContents.settings.style.display = "none";
}

function selectLobbySettingsMenu() {
    menuButtons.game.classList.remove("button-active")
    menuButtons.chat.classList.remove("button-active")
    menuButtons.settings.classList.add("button-active")

    menuButtons.gameIcon.classList.remove("button-active")
    menuButtons.chatIcon.classList.remove("button-active")
    menuButtons.settingsIcon.classList.add("button-active")

    menuContents.game.style.display = "none";
    menuContents.chat.style.display = "none";
    menuContents.settings.style.display = "block";
}

function sendMessage() {
    const chatInput = document.getElementById("chatInputField");
    const message = chatInput.value;
    chatInput.value = '';
    const messageWrapper = {
        playerToken: getCookie('playerToken'),
        message: {
            message: message,
            timestamp: new Date().getTime()
        }
    };
    stompClient.publish({
        destination: '/app/lobby/' + retrievedLobbyId + '/chat',
        body: JSON.stringify(messageWrapper)
    });
}

function appendChatMessage(data) {
    const chatMessages = document.getElementById("chatMessages");
    const message = document.createElement("div");
    message.classList.add("chatMessage");
    message.innerText = data.playerName + ': ' + data.message;
    chatMessages.appendChild(message);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}