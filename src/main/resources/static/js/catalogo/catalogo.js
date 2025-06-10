document.addEventListener("DOMContentLoaded", async () => {
    await carregarCatalogo();
});

function carregarCarrinho() {
    return JSON.parse(localStorage.getItem("carrinho")) || [];
}

function salvarCarrinho(carrinho) {
    localStorage.setItem("carrinho", JSON.stringify(carrinho));
}

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
        const precoBase = p.estoque?.at(-1)?.precoVenda || 0;
        const precoMatch = precoBase >= precoMin && precoBase <= precoMax;
        const categoriaMatch = categoriasSelecionadas.length === 0 ||
            p.categorias.some(cat => categoriasSelecionadas.includes(cat.nome));
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
    const container = document.getElementById("catalogo-container");
    container.innerHTML = "";

    for (const produto of produtos) {
        let precoVendaTexto = "Indisponível";
        let totalEstoque = 0;
        let precoVenda = null;

        try {
            const estoqueResp = await fetch(`http://localhost:8080/api/estoque/produto/${produto.id}`);
            if (estoqueResp.ok) {
                const estoques = await estoqueResp.json();
                totalEstoque = estoques.reduce((soma, e) => soma + e.quantidade, 0);

                if (estoques.length > 0) {
                    const maisRecente = estoques[estoques.length - 1];
                    precoVenda = maisRecente.precoVenda;
                    precoVendaTexto = "R$ " + parseFloat(precoVenda).toFixed(2);
                }
            }
        } catch (err) {
            console.warn("Erro ao buscar estoque:", err);
        }

        const imagemSrc = `http://localhost:8080/api/produtos/imagem/${produto.imagem}`;

        const card = document.createElement("div");
        card.classList.add("col-md-3", "mb-4");

        card.innerHTML = `
            <div class="card h-100">
                <img src="${imagemSrc}" class="card-img-top" style="height: 259px; object-fit: cover;" alt="Imagem do Produto">
                <div class="card-body text-center">
                    <h5 class="card-title">${produto.nome}</h5>
                    <p class="text-muted small">${produto.categorias.map(cat => cat.nome).join(" • ")}</p>
                    <p class="card-text">${precoVendaTexto}</p>
                    <button class="btn btn-dark btn-comprar" data-id="${produto.id}" ${totalEstoque <= 0 ? "disabled" : ""}>
                        ${totalEstoque <= 0 ? "Indisponível" : "Comprar"}
                    </button>
                </div>
            </div>
        `;

        container.appendChild(card);

        const btn = card.querySelector(".btn-comprar");
        if (btn) {
            btn.addEventListener("click", () => adicionarAoCarrinho(produto.id, precoVenda));
        }
    }
}

async function carregarCatalogo() {
    const container = document.getElementById("catalogo-container");
    container.innerHTML = "";

    try {
        const resposta = await fetch("http://localhost:8080/api/produtos");

        todosProdutos = (await resposta.json()).filter(p => p.status.toUpperCase() === "ATIVO");
        todosProdutos = todosProdutos.filter((prod, index, self) =>
            index === self.findIndex(p => p.id === prod.id)
        );
        todasCategorias = new Set();
        todosProdutos.forEach(p =>
            p.categorias.forEach(cat => todasCategorias.add(cat.nome))
        );

        preencherFiltros();
        await renderizarProdutos(todosProdutos);

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
        const estoques = await respEstoque.json();

        const totalEstoque = estoques.reduce((soma, item) => soma + item.quantidade, 0);

        if (totalEstoque <= 0) {
            return alert("Produto sem estoque.");
        }

        const estoqueMaisRecente = estoques[estoques.length - 1];
        const precoVenda = estoqueMaisRecente?.precoVenda ?? 0;

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