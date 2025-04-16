function carregarCarrinho() {
    return JSON.parse(localStorage.getItem("carrinho")) || [];
}

function salvarCarrinho(carrinho) {
    localStorage.setItem("carrinho", JSON.stringify(carrinho));
}

document.addEventListener("DOMContentLoaded", async () => {
    await carregarCatalogo(); // carrega catálogo e categorias
});

let todasCategorias = new Set();
let todosProdutos = [];

function preencherFiltros() {
    const divFiltros = document.getElementById("filtrosCategorias");
    divFiltros.innerHTML = "";

    [...todasCategorias].sort().forEach(cat => {
        const id = `cat_${cat.replace(/\s+/g, "_")}`;
        divFiltros.innerHTML += `
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="checkbox" id="${id}" value="${cat}">
              <label class="form-check-label" for="${id}">${cat}</label>
            </div>
        `;
    });

    document.querySelectorAll("#filtrosCategorias input").forEach(cb =>
        cb.addEventListener("change", aplicarFiltro)
    );
}

function aplicarFiltro() {
    const nomeBusca = document.getElementById("buscaProduto")?.value?.toLowerCase() || "";
    const precoMin = parseFloat(document.getElementById("precoMin")?.value) || 0;
    const precoMax = parseFloat(document.getElementById("precoMax")?.value) || Infinity;
    const categoriasSelecionadas = Array.from(
        document.querySelectorAll("#filtrosCategorias input:checked")
    ).map(cb => cb.value);

    const filtrados = todosProdutos.filter(p => {
        const nomeMatch = p.nome.toLowerCase().includes(nomeBusca);
        const precoMatch = p.precoVenda >= precoMin && p.precoVenda <= precoMax;
        const categoriaMatch = categoriasSelecionadas.length === 0 ||
            p.categorias.some(cat => categoriasSelecionadas.includes(cat));
        return nomeMatch && precoMatch && categoriaMatch;
    });

    renderizarProdutos(filtrados);
}

function limparFiltros() {
    document.getElementById("buscaProduto").value = "";
    document.getElementById("precoMin").value = "";
    document.getElementById("precoMax").value = "";

    document.querySelectorAll("#filtrosCategorias input[type=checkbox]").forEach(cb => cb.checked = false);

    aplicarFiltro();
}

async function renderizarProdutos(produtos) {
    const container = document.getElementById("catalogoProdutos");
    container.innerHTML = "";

    for (const produto of produtos) {
        const estoqueResp = await fetch(`http://localhost:8080/api/estoque/produto/${produto.id}`);
        const estoque = await estoqueResp.json();
        const totalEstoque = estoque.reduce((soma, item) => soma + item.quantidade, 0);

        const card = document.createElement("div");
        card.classList.add("col-md-3", "mb-4");

        card.innerHTML = `
            <div class="card h-100">
                <img src="/${produto.imagem}" class="card-img-top" style="height: 259px; object-fit: cover;" alt="Imagem do Produto">
                <div class="card-body text-center">
                    <h5 class="card-title">${produto.nome}</h5>
                    <p class="text-muted small">${produto.categorias.join(" • ")}</p>
                    <p class="card-text">R$ ${produto.precoVenda.toFixed(2)}</p>
                    <button class="btn btn-dark btn-comprar" data-id="${produto.id}" ${totalEstoque <= 0 ? "disabled" : ""}>
                        ${totalEstoque <= 0 ? "Indisponível" : "Comprar"}
                    </button>
                </div>
            </div>
        `;

        container.appendChild(card);

        // agora sim: evento dentro do mesmo escopo
        const btn = card.querySelector(".btn-comprar");
        if (btn) {
            btn.addEventListener("click", () => adicionarAoCarrinho(produto.id));
        }
    }
}

async function carregarCatalogo() {
    const container = document.getElementById("catalogoProdutos");
    container.innerHTML = "";

    try {
        const resposta = await fetch("http://localhost:8080/api/produtos");
        todosProdutos = await resposta.json();

        // capturar categorias únicas
        todasCategorias = new Set();
        todosProdutos.forEach(p => p.categorias.forEach(cat => todasCategorias.add(cat)));

        preencherFiltros(); // renderiza os filtros
        renderizarProdutos(todosProdutos); // renderiza todos

    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
    }
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
        const estoque = await respEstoque.json();
        const totalEstoque = estoque.reduce((soma, item) => soma + item.quantidade, 0);

        if (totalEstoque <= 0) {
            return alert("Produto sem estoque.");
        }

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
                precoVenda: produto.precoVenda,
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