let historicoMensagens = [
    { role: "system", content: "Você é o VortisBot, assistente virtual da Vortis Mobile." }
];

function toggleChat() {
    const window = document.getElementById("chatbotWindow");
    const chat = document.getElementById("chatbotContent");

    if (window.style.display === "block") {
        window.style.display = "none";
    } else {
        window.style.display = "block";

        if (chat.innerHTML.trim() === "") {
            const msg = "Olá! 👋 Como posso te ajudar hoje na Vortis Mobile?";
            chat.innerHTML += `<div><b>VortisBot:</b> ${msg}</div>`;
            historicoMensagens.push({ role: "assistant", content: msg });
        }
    }
}

function sendMessage() {
    const input = document.getElementById("userMessage");
    const message = input.value.trim();
    const chat = document.getElementById("chatbotContent");

    if (message === "") return;

    const idCliente = sessionStorage.getItem("idCliente");

    if (!idCliente) {
        const error = document.createElement("div");
        error.innerHTML = "<b>Erro:</b> Cliente não identificado. Faça login antes de usar o assistente.";
        chat.appendChild(error);
        return;
    }

    // Exibe a mensagem do usuário no chat
    const userMsg = document.createElement("div");
    userMsg.innerHTML = "<b>Você:</b> " + message;
    chat.appendChild(userMsg);

    // Adiciona a mensagem do usuário ao histórico
    historicoMensagens.push({ role: "user", content: message });
    input.value = "";

    // Envia histórico acumulado (sem a mensagem system duplicada)
    const mensagensFiltradas = historicoMensagens.filter(m => m.role !== "system");

    fetch("/api/chatbot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            idCliente: idCliente,
            mensagens: mensagensFiltradas
        })
    })
        .then(res => res.json())
        .then(data => {
            const resposta = data.resposta || "Desculpe, não consegui entender sua solicitação.";
            const botMsg = document.createElement("div");
            const respostaFormatada = resposta
                .replace(/\n/g, "<br>")
                .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");

            botMsg.innerHTML = "<b>VortisBot:</b> " + respostaFormatada;

            chat.appendChild(botMsg);
            historicoMensagens.push({ role: "assistant", content: resposta });
        })
        .catch(() => {
            const error = document.createElement("div");
            error.innerHTML = "<b>Erro:</b> Não foi possível responder agora.";
            chat.appendChild(error);
        });
}