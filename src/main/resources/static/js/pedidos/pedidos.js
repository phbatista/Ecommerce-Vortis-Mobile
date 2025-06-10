document.addEventListener("DOMContentLoaded", async () => {
    const tabela = document.getElementById("tabelaPedidos");

    try {
        const idCliente = sessionStorage.getItem("idCliente");
        const resp = await fetch(`http://localhost:8080/api/vendas/meus-pedidos/${idCliente}`);
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
                    <button class="btn btn-sm btn-outline-dark" data-bs-toggle="collapse" data-bs-target="#${collapseId}">
                        ▼
                    </button>
                </td>
                <td>${p.id}</td>
                <td>${new Date(p.dataVenda).toLocaleString()}</td>
                <td>${p.status}</td>
                <td>
                    ${p.status === 'ENTREGUE' ? `
                        <button class="btn btn-sm btn-outline-primary" onclick="abrirFormularioTroca(${p.id})">
                            Trocar Produto
                        </button>
                        <button class="btn btn-sm btn-outline-danger ms-2" onclick="abrirFormularioDevolucao(${p.id})">
                            Devolver Produto
                        </button>
                    ` : '-'}
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

async function abrirFormularioTroca(idPedido) {
    const resp = await fetch(`http://localhost:8080/api/vendas/${idPedido}`);
    const pedido = await resp.json();

    let html = '';
    pedido.produtos.forEach((item, index) => {
        html += `
            <div class="mb-2">
                <label><strong>${item.produtoNome}</strong></label><br>
                <input type="number" min="0" max="${item.quantidade}" value="0" 
                    data-id-produto="${item.idProduto}" class="form-control form-control-sm troca-qtd" placeholder="Qtd a trocar">
            </div>`;
    });

    Swal.fire({
        title: 'Solicitar Troca',
        html,
        confirmButtonText: 'Confirmar Troca',
        preConfirm: () => {
            const itens = Array.from(document.querySelectorAll('.troca-qtd'))
                .map(input => ({
                    idProduto: input.dataset.idProduto,
                    quantidade: parseInt(input.value)
                }))
                .filter(i => i.quantidade > 0);
            return itens;
        }
    }).then(async result => {
        if (result.isConfirmed && result.value.length > 0) {
            console.log("Payload enviado:", JSON.stringify({
                idPedido,
                tipo: "TROCA",
                itens: result.value.map(i => ({
                    idProduto: Number(i.idProduto),
                    quantidade: i.quantidade,
                    motivo: "Solicitado pelo cliente"
                }))
            }, null, 2));

            await fetch("http://localhost:8080/api/vendas/solicitar-troca-ou-devolucao", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    idPedido,
                    tipo: "TROCA",
                    itens: result.value.map(i => ({
                        idProduto: Number(i.idProduto), // Certifique que é número
                        quantidade: i.quantidade,
                        motivo: "Solicitado pelo cliente"
                    }))
                })
            });
            Swal.fire("Solicitado!", "Seu pedido de troca foi registrado.", "success").then(() => location.reload());
        }
    });
}

async function abrirFormularioDevolucao(idPedido) {
    const resp = await fetch(`http://localhost:8080/api/vendas/${idPedido}`);
    const pedido = await resp.json();

    let html = '';
    pedido.produtos.forEach((item, index) => {
        html += `
            <div class="mb-2">
                <label><strong>${item.produtoNome}</strong></label><br>
                <input type="number" min="0" max="${item.quantidade}" value="0" 
                    data-id-produto="${item.idProduto}" class="form-control form-control-sm devolucao-qtd" placeholder="Qtd a devolver">
            </div>`;
    });

    Swal.fire({
        title: 'Solicitar Devolução',
        html,
        confirmButtonText: 'Confirmar Devolução',
        preConfirm: () => {
            const itens = Array.from(document.querySelectorAll('.devolucao-qtd'))
                .map(input => ({
                    idProduto: Number(input.dataset.idProduto),
                    quantidade: parseInt(input.value)
                }))
                .filter(i => i.quantidade > 0);
            return itens;
        }
    }).then(async result => {
        if (result.isConfirmed && result.value.length > 0) {
            await fetch("http://localhost:8080/api/vendas/solicitar-troca-ou-devolucao", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    idPedido,
                    tipo: "DEVOLUCAO",
                    itens: result.value
                })
            });
            Swal.fire("Solicitado!", "Sua devolução foi registrada.", "success").then(() => location.reload());
        }
    });
}
