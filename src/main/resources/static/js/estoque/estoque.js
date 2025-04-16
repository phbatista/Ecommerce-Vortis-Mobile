document.addEventListener("DOMContentLoaded", () => {
    carregarProdutos();

    document.getElementById("formEstoque").addEventListener("submit", async (e) => {
        e.preventDefault();

        const estoque = {
            idProduto: parseInt(document.getElementById("produto").value),
            dataEntrada: document.getElementById("dataEntrada").value,
            fornecedor: document.getElementById("fornecedor").value,
            custoUnitario: parseFloat(document.getElementById("custoUnitario").value),
            quantidade: parseInt(document.getElementById("quantidade").value)
        };

        if (!estoque.idProduto) {
            alert("Selecione um produto.");
            return;
        }

        try {
            const resp = await fetch("http://localhost:8080/api/estoque", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(estoque)
            });

            if (resp.ok) {
                alert("Estoque cadastrado com sucesso!");
                document.getElementById("formEstoque").reset();
            } else {
                const erro = await resp.text();
                alert("Erro ao cadastrar estoque: " + erro);
            }
        } catch (err) {
            console.error("Erro ao cadastrar estoque:", err);
            alert("Erro ao cadastrar estoque.");
        }
    });
});

async function carregarProdutos() {
    try {
        const resposta = await fetch("http://localhost:8080/api/produtos");
        const produtos = await resposta.json();

        const select = document.getElementById("produto");
        produtos.forEach(p => {
            const option = document.createElement("option");
            option.value = p.id;
            option.textContent = p.nome;
            select.appendChild(option);
        });
    } catch (err) {
        console.error("Erro ao carregar produtos:", err);
    }
}