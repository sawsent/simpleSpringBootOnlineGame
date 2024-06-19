document.addEventListener("DOMContentLoaded", function() {
    const connectButton = document.getElementById("connectButton");
    connectButton.onclick = connect;

    const readyButton = document.getElementById("readyButton");
    readyButton.onclick = getReady;

    let stompClient = null;
    let username = null;
    let table = null;


    function getReady() {
        if (isReady()) {
            alert('you are already ready');
            return;
        }
        alert('you have readied up!')
        stompClient.send("/app/ready", {}, username);
    }

    function connect() {
        username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);

            document.getElementById('login').style.display = 'none';

            stompClient.subscribe('/user/' + username + '/queue/hand', function (message) {
                const hand = JSON.parse(message.body);
                console.log(hand);
                displayHand(hand.cards);
            });

            stompClient.subscribe('/topic/game-updates', function (message) {
                table = JSON.parse(message.body);
                console.log('Received meta info: ', table.metaInfo);
                // Update UI with the meta information if needed
                const infoDiv = document.getElementById('info');
                infoDiv.innerHTML = `<p> ${JSON.stringify(table)} </p>`;
                
            });

            stompClient.send("/app/connect", {}, JSON.stringify({'username': username, 'password': password, 'ready': false}));
        });
    }

    function isYourTurn() {
        return table.metaInfo.gameState !== 'WAITING_FOR_PLAYERS' && table.metaInfo.currentPlayer.username === username;
    }

    function isReady() {
        return table.metaInfo.players.filter(player => player.username == username)[0].ready;
    }

    function sendPlay(cardElement) {
        return function() {

            let card = cardElement.innerText;

            alert(`card: ${card} clicked!`)

            if (!isYourTurn()) {
                alert('not your turn yet!');
                return;
            }
            
            cardElement.parentNode.removeChild(cardElement);
            stompClient.send("/app/play", {}, JSON.stringify({'username': username, 'card': card}));
        }
    }

    function displayHand(hand) {
        const handDiv = document.getElementById('cards');
        handDiv.innerHTML = '<h3>Your Hand</h3>';
        hand.forEach(card => {
            const cardElement = document.createElement('button');
            cardElement.className = 'card';
            cardElement.innerText = card;
            cardElement.onclick = sendPlay(cardElement);
            handDiv.appendChild(cardElement);
        });
    }
});
