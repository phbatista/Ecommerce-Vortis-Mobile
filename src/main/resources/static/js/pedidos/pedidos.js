document.addEventListener("DOMContentLoaded", async () => {
    const tabela = document.getElementById("tabelaPedidos");

    try {
        const resp = await fetch("http://localhost:8080/api/vendas/admin");
        const pedidos = await resp.json();

        if (!pedidos.length) {
            tabela.innerHTML = `<tr><td colspan="5" class="text-center">Nenhum pedido encontrado.</td></tr>`;
            return;
        }

        pedidos.forEach(p => {
            const collapseId = `detalhes-${p.id}`;

            const linha = document.createElement("tr");
            linha.innerHTML = `
                <td>
                    <button class="btn btn-sm btn-outline-dark" type="button" data-bs-toggle="collapse" data-bs-target="#${collapseId}" aria-expanded="false" aria-controls="${collapseId}">
                        ▼
                    </button>
                </td>
                <td>${p.id}</td>
                <td>${new Date(p.dataVenda).toLocaleString()}</td>
                <td>${p.status}</td>
                <td>
                    ${p.status === 'ENTREGUE' ? `
                        <button class="btn btn-sm btn-outline-primary" onclick="abrirFormularioTroca(${p.id})">
                            Solicitar Troca/Devolução
                        </button>` : '-'}
                </td>
            `;

            const detalhes = document.createElement("tr");
            detalhes.innerHTML = `
                <td colspan="5" class="p-0 border-0">
                    <div id="${collapseId}" class="collapse p-3 bg-light">
                        <p><strong>Produtos:</strong><br>
                            ${p.produtos.map(i => `- ${i.produtoNome} (Qtd: ${i.quantidade}, R$ ${i.preco.toFixed(2)})`).join("<br>")}
                        </p>
                        <p><strong>Endereço:</strong><br>
                            ${p.endereco}
                        </p>
                        <p><strong>Cartões:</strong><br>
                            ${p.cartoes.map(c => `${c.finalNumero} - R$ ${c.valor.toFixed(2)}`).join("<br>")}
                        </p>
                        <p>
                            <strong>Cupom Promocional:</strong> ${p.cupomPromocional || "-"}<br>
                            <strong>Cupom de Troca:</strong> ${p.cupomTroca || "-"}
                        </p>
                        <p>
                            <strong>Frete:</strong> R$ ${p.frete.toFixed(2)}<br>
                            <strong>Total:</strong> <strong>R$ ${p.total.toFixed(2)}</strong>
                        </p>
                    </div>
                </td>
            `;

            tabela.appendChild(linha);
            tabela.appendChild(detalhes);
        });

    } catch (err) {
        console.error("Erro ao carregar pedidos:", err);
        tabela.innerHTML = `<tr><td colspan="5" class="text-center">Erro ao carregar pedidos.</td></tr>`;
    }
});