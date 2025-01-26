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
    showFeedbackPopup("Link copiato!");
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
    resetResignButton();

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
            message: message
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

function askForDraw() {
    sendDrawRequest();
}

function sendDrawRequest() {
    const messageWrapper = {
        playerToken: getCookie('playerToken'),
        message: {
            accept: true
        }
    };

    stompClient.publish({
        destination: '/app/lobby/' + retrievedLobbyId + '/draw',
        body: JSON.stringify(messageWrapper)
    });
    showFeedbackPopup("Richiesta inviata");
}

function acceptDraw() {
    sendDrawRequest();
    showFeedbackPopup("Patta accettata");
}

function declineDraw() {
    sendDrawDeny()
    showFeedbackPopup("Patta Rifiutata");
}

function sendDrawDeny() {
    const messageWrapper = {
        playerToken: getCookie('playerToken'),
        message: {
            accept: false
        }
    };

    stompClient.publish({
        destination: '/app/lobby/' + retrievedLobbyId + '/draw',
        body: JSON.stringify(messageWrapper)
    });
}

function resetResignButton() {
    document.getElementById("endGameResignButton").innerText = "Arrenditi";
    document.getElementById("endGameResignButton").onclick = resign;
}

function resign() {
    document.getElementById("endGameResignButton").innerText = "Confermare?";
    document.getElementById("endGameResignButton").onclick = confirmResign;
}

function confirmResign() {
    sendSocketResign();
}

function sendSocketResign() {
    const messageWrapper = {
        playerToken: getCookie('playerToken')
    };

    stompClient.publish({
        destination: '/app/lobby/' + retrievedLobbyId + '/resign',
        body: JSON.stringify(messageWrapper)
    });
}

function showNotification(data) {
    document.getElementById("notification-header").innerText = data.title;
    document.getElementById("notification-body").innerText = data.message;

    switchNotificationType(data.type);

    document.getElementById("notification-content").style.display = "block";
    setTimeout(() => {
        closeNotification(); //TODO: buggato se si sovrappongono più notifiche non si resetta il timer
    }, 5000);

    function switchNotificationType(type) {
        switch (type) {
            case "DRAW_REQUEST":
                document.getElementById("notification-footer").innerHTML = '<button onclick="acceptDraw()">Accetta</button><button onclick="declineDraw()">Rifiuta</button>';
                break;
                //TODO: Add more cases for different types of notifications
        }
    }
}

function closeNotification() {
    document.getElementById("notification-content").style.display = "none";
}

function showFeedbackPopup(text) {
    document.getElementById("feedback-popup").innerText = text;
    document.getElementById("feedback-popup").style.display = "block";
    setTimeout(() => {
        closeFeedbackPopup();
    }, 1000);
}

function closeFeedbackPopup() {
    const feedbackPopup = document.getElementById("feedback-popup");
    feedbackPopup.style.animation = "fadeOut 0.5s ease-in-out";
    setTimeout(() => {
        feedbackPopup.style.display = "none";
        feedbackPopup.style.animation = "fadeIn 0.5s ease-in-out"; // Reset animation
    }, 500);
}