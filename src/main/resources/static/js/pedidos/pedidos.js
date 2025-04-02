function gerarLinhaPedido(pedido, index) {
    const linhaResumo = `
    <tr>
      <td><button class="btn btn-sm btn-outline-dark" onclick="toggleDetalhes(${pedido.id})">▼</button></td>
      <td>${pedido.id}</td>
      <td>${new Date(pedido.dataVenda).toLocaleString()}</td>
      <td>R$ ${pedido.total.toFixed(2)}</td>
      <td>${pedido.status}</td>
      <td><button class="btn btn-sm btn-primary">Solicitar troca</button></td>
    </tr>
  `;

    const linhaDetalhes = `
    <tr id="detalhes-${pedido.id}" style="display: none;">
      <td colspan="6">
        <div class="text-start">
          ${pedido.itens.map(item => `
            <div><strong>Produto:</strong> ${item.nome} | Quantidade: ${item.quantidade} | Unitário: R$ ${item.precoUnitario.toFixed(2)}</div>
          `).join("")}
          <hr>
          <div><strong>Entrega:</strong> ${pedido.endereco}</div>
          <div><strong>Pagamento:</strong> ${pedido.pagamento}</div>
          <div><strong>Frete:</strong> R$ ${pedido.frete.toFixed(2)}</div>
          <div><strong>Valor Final:</strong> R$ ${pedido.total.toFixed(2)}</div>
        </div>
      </td>
    </tr>
  `;

    return linhaResumo + linhaDetalhes;
}

function toggleDetalhes(id) {
    const linha = document.getElementById(`detalhes-${id}`);
    linha.style.display = linha.style.display === "none" ? "" : "none";
}

async function carregarPedidos() {
    const idCliente = localStorage.getItem("idCliente");

    const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}/pedidos`);
    const pedidos = await resp.json();

    const tbody = document.getElementById("tabelaPedidos");
    tbody.innerHTML = "";

    pedidos.forEach((p, i) => {
        tbody.innerHTML += gerarLinhaPedido(p, i);
    });
}