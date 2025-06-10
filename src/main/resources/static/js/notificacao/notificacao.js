document.addEventListener("DOMContentLoaded", async () => {
    const idCliente = sessionStorage.getItem("idCliente");
    if (!idCliente) return;

    const contador = document.getElementById("contadorNotificacoes");
    const container = document.getElementById("listaNotificacoes");

    try {
        const resp = await fetch(`http://localhost:8080/api/notificacoes/${idCliente}`);
        if (!resp.ok) throw new Error("Erro ao buscar notificações");

        const notificacoes = await resp.json();

        if (notificacoes.length > 0 && contador) {
            contador.textContent = notificacoes.length;
            contador.style.display = "inline-block";
        }

        container.innerHTML = notificacoes.map(n => `
    <li class="list-group-item">
        <strong>
            ${n.tipo} - ${new Date(n.data).toLocaleString("pt-BR", {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        })}
        </strong><br>
        ${n.mensagem}
    </li>
`).join("");

    } catch (e) {
        console.error("Erro ao carregar notificações:", e);
    }
});