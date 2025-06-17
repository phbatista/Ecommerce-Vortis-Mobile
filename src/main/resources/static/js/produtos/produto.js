document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const idProduto = params.get("id");
    const container = document.getElementById("detalhesProduto");

    if (!idProduto) {
        container.innerHTML = "<p>Produto não informado.</p>";
        return;
    }

    try {
        const [respProduto, respEstoque] = await Promise.all([
            fetch(`http://localhost:8080/api/produtos/${idProduto}`),
            fetch(`http://localhost:8080/api/estoque/produto/${idProduto}`)
        ]);

        if (!respProduto.ok || !respEstoque.ok) {
            container.innerHTML = "<p>Erro ao carregar produto.</p>";
            return;
        }

        const produto = await respProduto.json();
        const estoques = await respEstoque.json();

        const totalEstoque = estoques.reduce((soma, item) => soma + item.quantidade, 0);
        const estoqueMaisRecente = estoques[estoques.length - 1];
        const precoVenda = estoqueMaisRecente?.precoVenda ?? 0;

        const imagemSrc = `http://localhost:8080/api/produtos/imagem/${produto.imagem}`;

        container.innerHTML = `
    <div class="row align-items-center">
        <div class="col-md-5 text-center">
            <img src="${imagemSrc}" class="img-fluid rounded shadow" style="max-height: 750px;" alt="${produto.nome}">
        </div>
        <div class="col-md-7">
            <h2 class="fw-bold mb-3">${produto.nome}</h2>
            <ul class="list-unstyled fs-5">
                <li><strong>Categorias:</strong> ${produto.categorias.map(c => c.nome).join(", ")}</li>
                <li><strong>Armazenamento:</strong> ${produto.armazenamento}</li>
                <li><strong>Memória RAM:</strong> ${produto.memoriaRam}</li>
                <li><strong>Sistema Operacional:</strong> ${produto.sistemaOperacional}</li>
                <li><strong>Chipset:</strong> ${produto.chipset}</li>
                <li><strong>Bateria:</strong> ${produto.bateria}</li>
                <li><strong>Resolução Câmera:</strong> ${produto.resolucaoCamera}</li>
                <li><strong>GPU:</strong> ${produto.gpu}</li>
                <li><strong>Tela:</strong> ${produto.tipoTela} (${produto.tamanhoTela}″)</li>
                <li><strong>Dimensões:</strong> ${produto.altura} × ${produto.largura} × ${produto.profundidade} cm • ${produto.peso}g</li>
                <li><strong>Preço:</strong> R$ ${precoVenda.toFixed(2)}</li>
            </ul>
            <button class="btn btn-primary px-4" onclick="adicionarAoCarrinho(${produto.id})" ${totalEstoque <= 0 ? "disabled" : ""}>
                ${totalEstoque <= 0 ? "Indisponível" : "Adicionar ao Carrinho"}
            </button>
        </div>
    </div>
`;

    } catch (err) {
        console.error("Erro ao carregar dados:", err);
        container.innerHTML = "<p>Erro ao carregar o produto.</p>";
    }
});

// Funções de carrinho (iguais ao catálogo)
function carregarCarrinho() {
    return JSON.parse(localStorage.getItem("carrinho")) || [];
}

function salvarCarrinho(carrinho) {
    localStorage.setItem("carrinho", JSON.stringify(carrinho));
}

async function adicionarAoCarrinho(idProduto) {
    try {
        const [respProduto, respEstoque] = await Promise.all([
            fetch(`http://localhost:8080/api/produtos/${idProduto}`),
            fetch(`http://localhost:8080/api/estoque/produto/${idProduto}`)
        ]);

        if (!respProduto.ok || !respEstoque.ok) {
            return alert("Erro ao buscar produto ou estoque.");
        }

        const produto = await respProduto.json();
        const estoques = await respEstoque.json();

        const totalEstoque = estoques.reduce((soma, item) => soma + item.quantidade, 0);
        if (totalEstoque <= 0) {
            return alert("Produto sem estoque.");
        }

        const precoVenda = estoques.at(-1)?.precoVenda ?? 0;
        let carrinho = carregarCarrinho();
        let item = carrinho.find(p => p.id === produto.id);

        if (item) {
            if (item.quantidade + 1 > totalEstoque) {
                return alert("Quantidade excede o estoque disponível.");
            }
            item.quantidade += 1;
        } else {
            carrinho.push({
                id: produto.id,
                nome: produto.nome,
                precoVenda: precoVenda,
                quantidade: 1
            });
        }

        salvarCarrinho(carrinho);
        alert("Produto adicionado ao carrinho!");
    } catch (err) {
        console.error("Erro ao adicionar ao carrinho:", err);
        alert("Erro ao adicionar o produto ao carrinho.");
    }
}