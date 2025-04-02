let editandoId = null;

async function carregarCupons() {
    const resp = await fetch("http://localhost:8080/api/cupons");
    const cupons = await resp.json();

    const tbody = document.getElementById("tabelaCupons");
    tbody.innerHTML = "";

    cupons.forEach(c => {
        tbody.innerHTML += `
      <tr>
        <td>${c.id}</td>
        <td>${c.codigo}</td>
        <td>R$ ${c.valor.toFixed(2)}</td>
        <td>${c.tipo}</td>
        <td>${c.cliente?.id || "-"}</td>
        <td>${c.dataValidade || "-"}</td>
        <td>${c.ativo ? "Sim" : "Não"}</td>
        <td>
          <button class="btn btn-sm btn-primary" onclick='editarCupom(${JSON.stringify(c)})'>Editar</button>
          <button class="btn btn-sm btn-danger" onclick="deletarCupom(${c.id})">Excluir</button>
        </td>
      </tr>
    `;
    });
}

async function salvarCupom() {
    const cupom = {
        codigo: document.getElementById("codigo").value,
        valor: parseFloat(document.getElementById("valor").value),
        tipo: document.getElementById("tipo").value,
        dataValidade: document.getElementById("validade").value || null,
        ativo: document.getElementById("ativo").checked,
        cliente: document.getElementById("tipo").value === "TROCA"
            ? { id: Number(document.getElementById("clienteId").value) }
            : null
    };

    const metodo = editandoId ? "PUT" : "POST";
    const url = editandoId
        ? `http://localhost:8080/api/cupons/${editandoId}`
        : "http://localhost:8080/api/cupons";

    const resp = await fetch(url, {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cupom)
    });

    if (resp.ok) {
        alert("Cupom salvo com sucesso!");
        resetarFormulario();
        carregarCupons();
    } else {
        alert("Erro ao salvar cupom.");
    }
}

function editarCupom(c) {
    document.getElementById("codigo").value = c.codigo;
    document.getElementById("valor").value = c.valor;
    document.getElementById("tipo").value = c.tipo;
    document.getElementById("validade").value = c.dataValidade || "";
    document.getElementById("ativo").checked = c.ativo;
    document.getElementById("clienteId").value = c.cliente?.id || "";
    editandoId = c.id;
    document.getElementById("tituloFormulario").textContent = "Editar Cupom";
}

function resetarFormulario() {
    editandoId = null;
    document.getElementById("tituloFormulario").textContent = "Novo Cupom";
    document.getElementById("codigo").value = "";
    document.getElementById("valor").value = "";
    document.getElementById("tipo").value = "PROMOCIONAL";
    document.getElementById("clienteId").value = "";
    document.getElementById("validade").value = "";
    document.getElementById("ativo").checked = true;
}

async function deletarCupom(id) {
    if (!confirm("Deseja excluir este cupom?")) return;

    await fetch(`http://localhost:8080/api/cupons/${id}`, { method: "DELETE" });
    carregarCupons();
}

document.addEventListener("DOMContentLoaded", carregarCupons);