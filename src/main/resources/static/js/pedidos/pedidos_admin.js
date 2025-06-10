document.addEventListener("DOMContentLoaded", async () => {
    const tabela = document.getElementById("tabelaGerenciarPedidos");

    try {
        const resp = await fetch("http://localhost:8080/api/vendas/admin");
        const pedidos = await resp.json();

        pedidos.forEach(p => {
            const linha = document.createElement("tr");

            const select = document.createElement("select");
            select.className = "form-select";

            const statusAtual = p.status;
            const opcoesPermitidas = {
                "EM PROCESSAMENTO": ["APROVADO", "REPROVADO"],
                "APROVADO": ["EM TRANSPORTE"],
                "EM TRANSPORTE": ["ENTREGUE"]
            };

            [
                "EM PROCESSAMENTO", "REPROVADO", "APROVADO", "CANCELADO", "EM TRANSPORTE", "ENTREGUE"
            ].forEach(status => {
                const opt = document.createElement("option");
                opt.value = status;
                opt.textContent = status;

                if (status === statusAtual) {
                    opt.selected = true;
                }

                if (status === statusAtual || (opcoesPermitidas[statusAtual] || []).includes(status)) {
                    select.appendChild(opt);
                }
            });

            const botao = document.createElement("button");
            botao.textContent = "Salvar";
            botao.className = "btn btn-success btn-sm";
            botao.onclick = async () => {
                const novoStatus = select.value;
                const confirmacao = confirm(`Deseja alterar o status do pedido ${p.id} para "${novoStatus}"?`);
                if (!confirmacao) return;

                try {
                    const resposta = await fetch(`http://localhost:8080/api/vendas/admin/${p.id}/status?status=${encodeURIComponent(novoStatus)}`, {
                        method: "PUT"
                    });

                    if (resposta.ok) {
                        alert("Status atualizado com sucesso!");
                        location.reload();
                    } else {
                        alert("Erro ao atualizar status.");
                    }
                } catch (err) {
                    alert("Erro ao comunicar com o servidor.");
                }
            };

            linha.innerHTML = `
        <td>${p.id}</td>
        <td>${p.clienteNome}</td>
        <td>${new Date(p.dataVenda).toLocaleString()}</td>
        <td>${p.status}</td>
    `;

            const tdSelect = document.createElement("td");
            tdSelect.appendChild(select);

            const tdBtn = document.createElement("td");
            tdBtn.appendChild(botao);

            linha.appendChild(tdSelect);
            linha.appendChild(tdBtn);
            tabela.appendChild(linha);
        });

    } catch (err) {
        console.error("Erro ao carregar pedidos:", err);
    }
});