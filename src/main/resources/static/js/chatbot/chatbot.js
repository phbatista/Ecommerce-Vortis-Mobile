function toggleChat() {
    const window = document.getElementById("chatbotWindow");
    const chat = document.getElementById("chatbotContent");

    if (window.style.display === "block") {
        window.style.display = "none";
    } else {
        window.style.display = "block";

        if (chat.innerHTML.trim() === "") {
            chat.innerHTML += "<div><b>VortisBot:</b> Olá! 👋 Como posso te ajudar hoje na Vortis Mobile?</div>";
        }
    }
}

function sendMessage() {
    const input = document.getElementById("userMessage");
    const message = input.value.trim();
    const chat = document.getElementById("chatbotContent");

    if (message === "") return;

    // Mostra a mensagem do usuário
    const userMsg = document.createElement("div");
    userMsg.innerHTML = "<b>Você:</b> " + message;
    chat.appendChild(userMsg);
    input.value = "";

    fetch("/api/chatbot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ mensagem: message })
    })
        .then(res => res.json())
        .then(data => {
            let resposta = data.resposta;
            const botMsg = document.createElement("div");
            botMsg.innerHTML = "<b>VortisBot:</b> " + resposta;
            chat.appendChild(botMsg);
        })
        .catch(() => {
            const error = document.createElement("div");
            error.innerHTML = "<b>Erro:</b> Não foi possível responder agora.";
            chat.appendChild(error);
        });
}