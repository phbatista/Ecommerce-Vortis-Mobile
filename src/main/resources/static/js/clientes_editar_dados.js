document.addEventListener("DOMContentLoaded", async function() {
    const urlParams = new URLSearchParams(window.location.search);
    const clienteId = urlParams.get("id");
    document.getElementById("clienteId").value = clienteId;
    document.getElementById("voltar").href += clienteId;

    if (clienteId) {
        try {
            let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}`);
            let cliente = await resposta.json();

            document.getElementById("nome").value = cliente.nome;
            document.getElementById("dataNascimento").value = cliente.dataNascimento;
            document.getElementById("genero").value = cliente.genero;
            document.getElementById("email").value = cliente.email;
            document.getElementById("tipoTelefone").value = cliente.telefone.tipo;
            document.getElementById("ddd").value = cliente.telefone.ddd;
            document.getElementById("telefone").value = cliente.telefone.numero;
        } catch (error) {
            console.error("Erro ao carregar os dados:", error);
            alert("Erro ao carregar os dados do cliente.");
        }
    } else {
        alert("ID do cliente não encontrado.");
    }
});

document.getElementById("formEditarDados").addEventListener("submit", async function(event) {
    event.preventDefault();

    const clienteId = new URLSearchParams(window.location.search).get("id");

    let cliente = {
        nome: document.getElementById("nome").value,
        dataNascimento: document.getElementById("dataNascimento").value,
        genero: document.getElementById("genero").value,
        email: document.getElementById("email").value,
        telefone: {
            tipo: document.getElementById("tipoTelefone").value,
            ddd: document.getElementById("ddd").value,
            numero: document.getElementById("telefone").value
        }
    };

    try {
        let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cliente)
        });

        if (resposta.ok) {
            alert("Dados atualizados com sucesso!");
            window.location.href = `/clientes_editar?id=${clienteId}`;
        } else {
            let erro = await resposta.json();
            alert("Erro ao atualizar: " + (erro.mensagem || "Erro desconhecido"));
        }
    } catch (error) {
        console.error("Erro ao enviar requisição:", error);
        alert("Erro ao conectar com o servidor.");
    }
});