var socket = new WebSocket("ws://localhost:8080/bol-stone-game/play");

var registered = false;

socket.onmessage = function (message) {
    var event = JSON.parse(message.data);
//    console.log(event);
    if (event.eventType === 'REGISTRATION_ACCEPTED') {
        document.getElementById("player").innerHTML = document.getElementById('playerName').value;
        registered = true;
    } else if (event.eventType === 'USER_REGISTERED') {
        var ul = document.getElementById("players");
        var children = ul.getElementsByTagName("li");
        var hasUser = false;
        for (var i = 0; i < children.length; i++) {
            if (children[i].innerText === event.newUser) {
                hasUser = true;
                break;
            }
        }
        if (!hasUser) {
            var li = document.createElement("li");
            li.appendChild(document.createTextNode(event.newUser));
            li.onclick = function () {
                sendInvite(event.newUser);
            }
            ul.appendChild(li);
        }
    } else if (event.eventType === 'USER_UNREGISTERED') {
        var ul = document.getElementById("players");
        var children = ul.getElementsByTagName("li");
        for (var i = 0; i < children.length; i++) {
            if (children[i].innerText === event.player) {
                ul.removeChild(children[i]);
                break;
            }
        }
    } else if (event.eventType === 'INVITE') {
        if (confirm('User ' + event.initiator + ' invited you to play a game.')) {
            invitationResponse(true, event);
        } else {
            invitationResponse(false, event);
        }
    }
    document.getElementById("messagesConsole").innerHTML += JSON.stringify(event) + "<br>";
}

function register(name) {
    var GameEvent = {
        eventType: "REGISTER",
        player: name
    }
    socket.send(JSON.stringify(GameEvent));
}

function sendInvite(user) {
    if (registered) {
        var GameEvent = {
            eventType: "INVITE",
            player: user
        }
        socket.send(JSON.stringify(GameEvent));
    }
}

function invitationResponse(accept, invitationEvent) {
    var GameEvent = {
        player: invitationEvent.player,
        initiator: invitationEvent.initiator
    }
    if (accept) {
        GameEvent.eventType = 'IVNITE_ACCEPT';
    } else {
        GameEvent.eventType = 'INVITE_REJECT';
    }
    socket.send(JSON.stringify(GameEvent));
}