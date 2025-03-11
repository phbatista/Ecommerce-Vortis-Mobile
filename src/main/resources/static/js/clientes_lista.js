let clientes = []; // 🔹 Armazena a lista de clientes globalmente

document.addEventListener("DOMContentLoaded", async function () {
    await carregarClientes(); // Carregar clientes ao carregar a página
});

// 🔹 Função para buscar clientes no backend
async function carregarClientes() {
    try {
        let resposta = await fetch("http://localhost:8080/api/clientes");
        clientes = await resposta.json(); // 🔹 Salva os clientes na variável global
        exibirClientes(clientes);
    } catch (error) {
        console.error("Erro ao carregar clientes:", error);
    }
}

// 🔹 Função para exibir clientes na tabela
function exibirClientes(lista) {
    let tabela = document.getElementById("listaClientes");
    tabela.innerHTML = ""; // 🔄 Limpa a tabela antes de renderizar

    lista.forEach(cliente => {
        let status = cliente.status === "1" ? "Ativado" : "Desativado";
        let telefone = cliente.telefone ? `${cliente.telefone.ddd} ${cliente.telefone.numero}` : "Não informado";

        let linha = document.createElement("tr");
        linha.innerHTML = `
            <td>${cliente.id}</td>
            <td>${cliente.nome}</td>
            <td>${cliente.cpf}</td>
            <td>${cliente.email}</td>
            <td>${telefone}</td>
            <td>${status}</td>
            <td>
                <button class="btn btn-success" onclick="ativarDesativarCliente(${cliente.id})">Ativar/Desativar</button>
                <button class="btn btn-warning" onclick="editarCliente(${cliente.id})">Editar</button>
                <button class="btn btn-danger" onclick="excluirCliente(${cliente.id})">Excluir</button>
            </td>
        `;
        tabela.appendChild(linha);
    });
}

// 🔍 Filtrar clientes
function filtrarClientes() {
    let campo = document.getElementById("filtroCampo").value;
    let valor = document.getElementById("filtroValor").value.toLowerCase();

    if (!valor) {
        alert("Digite um valor para pesquisar.");
        return;
    }

    let clientesFiltrados = clientes.filter(cliente => {
        let campoValor = cliente[campo];

        // 🔹 Se for telefone, ajusta o valor
        if (campo === "telefone" && cliente.telefone) {
            campoValor = `${cliente.telefone.ddd} ${cliente.telefone.numero}`;
        }

        // 🔹 Se for status, ajusta para "Ativado" ou "Desativado"
        if (campo === "status") {
            campoValor = cliente.status === "1" ? "Ativado" : "Desativado";
        }

        return campoValor && campoValor.toLowerCase().includes(valor);
    });

    exibirClientes(clientesFiltrados);
}

// 🔄 Restaurar a lista original
function limparFiltro() {
    document.getElementById("filtroValor").value = "";
    exibirClientes(clientes); // 🔹 Reexibe todos os clientes
}

async function ativarDesativarCliente(id) {
    if (confirm("Tem certeza que deseja alterar o status deste cliente?")) {
        try {
            let resposta = await fetch(`http://localhost:8080/api/clientes/${id}/status`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" }
            });

            if (resposta.ok) {
                alert("Status do cliente atualizado com sucesso!");
                location.reload(); // Atualiza a lista de clientes
            } else {
                alert("Erro ao atualizar status do cliente.");
            }
        } catch (error) {
            console.error("Erro ao alterar status do cliente:", error);
        }
    }
}

function editarCliente(id) {
    window.location.href = `/clientes_editar?id=${id}`;
}

function excluirCliente(id) {
    if (confirm("Tem certeza que deseja excluir este cliente?")) {
        fetch(`http://localhost:8080/api/clientes/${id}`, { method: "DELETE" })
            .then(() => location.reload())
            .catch(error => console.error("Erro ao excluir cliente:", error));
    }
}