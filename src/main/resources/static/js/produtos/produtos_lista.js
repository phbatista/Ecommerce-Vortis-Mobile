document.addEventListener("DOMContentLoaded", async () => {
    await carregarProdutos();
});

let todosProdutos = [];

async function carregarProdutos() {
    try {
        const resposta = await fetch("http://localhost:8080/api/produtos");
        todosProdutos = await resposta.json();
        renderizarTabela(todosProdutos);
    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
    }
}

function renderizarTabela(produtos) {
    const tbody = document.querySelector("table tbody");
    tbody.innerHTML = "";

    produtos.forEach(prod => {
        const btnStatus = prod.status.toUpperCase() === "ATIVO"
            ? `<button class="btn btn-sm btn-danger" onclick="abrirModalStatus(${prod.id}, 'inativo')">Inativar</button>`
            : `<button class="btn btn-sm btn-success" onclick="abrirModalStatus(${prod.id}, 'ativo')">Ativar</button>`;

        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td>${prod.nome}</td>
          <td>${prod.chipset}</td>
          <td>${prod.disponibilidadeAno}</td>
          <td>${prod.armazenamento}</td>
          <td>${prod.status}</td>
          <td>
            <button class="btn btn-sm btn-warning" onclick="editarProduto(${prod.id})">Editar</button>
            ${btnStatus}
          </td>
        `;
        tbody.appendChild(tr);
    });
}

function editarProduto(id) {
    window.location.href = `produtos_edicao?id=${id}`;
}

function abrirModalStatus(id, novoStatus) {
    document.getElementById("produtoIdModal").value = id;
    document.getElementById("novoStatusModal").value = novoStatus;
    document.getElementById("motivoStatus").value = "";
    document.getElementById("justificativaStatus").value = "";

    const modal = new bootstrap.Modal(document.getElementById("modalMotivoStatus"));
    modal.show();
}

async function confirmarAlteracaoStatus() {
    const id = document.getElementById("produtoIdModal").value;
    const status = document.getElementById("novoStatusModal").value;
    const motivo = document.getElementById("motivoStatus").value;
    const justificativa = document.getElementById("justificativaStatus").value;

    if (!motivo || !justificativa) {
        alert("Informe o motivo e a justificativa.");
        return;
    }

    try {
        const resposta = await fetch(`http://localhost:8080/api/produtos/${id}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status, motivo, justificativa })
        });

        if (resposta.ok) {
            bootstrap.Modal.getInstance(document.getElementById("modalMotivoStatus")).hide();
            await carregarProdutos();
        } else {
            const erro = await resposta.text();
            alert("Erro ao atualizar status: " + erro);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
    }
}

function filtrarClientes() {
    const nome = document.getElementById("filtroNome").value.trim().toLowerCase();
    const ano = document.getElementById("filtroAno").value.trim();
    const preco = document.getElementById("filtroPreco").value.trim();
    const armazenamento = document.getElementById("filtroArmazenamento").value.trim().toLowerCase();
    const status = document.getElementById("filtroStatus").value.trim().toLowerCase();

    const filtrados = todosProdutos.filter(prod => {
        return (!nome || prod.nome.toLowerCase().includes(nome)) &&
            (!ano || String(prod.disponibilidadeAno).includes(ano)) &&
            (!preco || String(prod.precoVenda).includes(preco)) &&
            (!armazenamento || prod.armazenamento.toLowerCase().includes(armazenamento)) &&
            (!status || prod.status.toLowerCase().includes(status));
    });

    renderizarTabela(filtrados);
}

function limparFiltro() {
    document.getElementById("filtroNome").value = "";
    document.getElementById("filtroAno").value = "";
    document.getElementById("filtroPreco").value = "";
    document.getElementById("filtroArmazenamento").value = "";
    document.getElementById("filtroStatus").value = "";
    renderizarTabela(todosProdutos);
}