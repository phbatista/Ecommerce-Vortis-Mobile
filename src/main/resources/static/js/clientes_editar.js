document.addEventListener("DOMContentLoaded", async function() {
    const urlParams = new URLSearchParams(window.location.search);
    const clienteId = urlParams.get("id");

    if (clienteId) {
        document.getElementById("clienteId").textContent = clienteId;

        try {
            let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}`);

            let cliente = await resposta.json();
            document.getElementById("clienteNome").textContent = cliente.nome;
        } catch (error) {
            alert(error.message);
        }
    }
});

function redirecionar(tipo) {
    const clienteId = new URLSearchParams(window.location.search).get("id");

    let destino = "";
    switch (tipo) {
        case "dados":
            destino = `/clientes_editar_dados?id=${clienteId}`;
            break;
        case "enderecos":
            destino = `/clientes_editar_enderecos?id=${clienteId}`;
            break;
        case "cartoes":
            destino = `/clientes_editar_cartoes?id=${clienteId}`;
            break;
        case "senha":
            destino = `/clientes_editar_senha?id=${clienteId}`;
            break;
        default:
            alert("Opção inválida.");
            return;
    }
    window.location.href = destino;
}