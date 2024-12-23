const lobbyInfo = document.getElementById("lobbyInfo");

lobbyInfo.addEventListener("resize", function() { //TODO: non listena una ceppa, da mettere ResizeObserver
    if(lobbyInfo.clientWidth < 500) {
        document.getElementById("topnav-icon").style.display = "block";
        document.getElementById("topnav-label").style.display = "none";
    } else {
        document.getElementById("topnav-icon").style.display = "none";
        document.getElementById("topnav-label").style.display = "block";
    }
});