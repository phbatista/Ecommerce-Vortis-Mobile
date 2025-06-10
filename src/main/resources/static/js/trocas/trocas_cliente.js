document.addEventListener("DOMContentLoaded", async () => {
    const idCliente = sessionStorage.getItem("idCliente");
    const tabela = document.getElementById("tabelaTrocas");

    const resp = await fetch(`http://localhost:8080/api/vendas/cliente/${idCliente}`);
    const trocas = await resp.json();

    if (!trocas.length) {
        tabela.innerHTML = `<tr><td colspan="6">Nenhuma solicitação encontrada.</td></tr>`;
        return;
    }

    trocas.forEach(t => {
        const linha = document.createElement("tr");
        linha.innerHTML = `
      <td>${t.id}</td>
      <td>${t.idPedido}</td>
      <td>${t.tipo}</td>
      <td>${t.status}</td>
      <td>${new Date(t.dataSolicitacao).toLocaleString()}</td>
      <td>${t.itens.map(i => `${i.nome} (Qtd: ${i.quantidade})`).join("<br>")}</td>
    `;
        tabela.appendChild(linha);
    });
});