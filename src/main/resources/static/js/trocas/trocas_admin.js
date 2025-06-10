document.addEventListener("DOMContentLoaded", async () => {
    const tabela = document.getElementById("tabelaAdminTrocas");

    try {
        const resp = await fetch("http://localhost:8080/api/vendas/trocas");
        const trocas = await resp.json();

        if (!trocas.length) {
            tabela.innerHTML = `<tr><td colspan="7">Nenhuma solicitação encontrada.</td></tr>`;
            return;
        }

        trocas.forEach(t => {
            const linha = document.createElement("tr");

            const itensHtml = (Array.isArray(t.itens) ? t.itens : [])
                .map(i => `${i.nomeProduto} (Qtd: ${i.quantidade})`)
                .join("<br>");

            linha.innerHTML = `
        <td>${t.id}</td>
        <td>${t.idPedido}</td>
        <td>${t.tipo}</td>
        <td>${t.status}</td>
        <td>${new Date(t.dataSolicitacao).toLocaleString()}</td>
        <td>${itensHtml}</td>
        <td>
          ${t.status === 'TROCA SOLICITADA' || t.status === 'DEVOLUÇÃO SOLICITADA' ? `
            <button class="btn btn-sm btn-success me-2" onclick="confirmarRecebimento(${t.id})">Confirmar Recebimento</button>
          ` : '-'}
        </td>
      `;

            tabela.appendChild(linha);
        });

    } catch (err) {
        console.error("Erro ao carregar trocas:", err);
        tabela.innerHTML = `<tr><td colspan="7">Erro ao carregar dados.</td></tr>`;
    }
});

async function confirmarRecebimento(idTroca) {
    const { isConfirmed } = await Swal.fire({
        title: "Repor itens no estoque?",
        text: "Deseja retornar os produtos ao estoque após o recebimento?",
        showCancelButton: true,
        confirmButtonText: "Sim, repor",
        cancelButtonText: "Não repor"
    });

    const retornar = isConfirmed;

    await fetch(`http://localhost:8080/api/vendas/trocas/${idTroca}/confirmar?retornarEstoque=${retornar}`, {
        method: "POST"
    });

    Swal.fire("Sucesso!", "Recebimento confirmado.", "success").then(() => location.reload());
}