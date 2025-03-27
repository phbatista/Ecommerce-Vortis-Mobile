document.addEventListener("DOMContentLoaded", async () => {
    const container = document.getElementById("catalogoProdutos");

    try {
        const resposta = await fetch("http://localhost:8080/api/produtos");
        const produtos = await resposta.json();

        produtos.forEach(produto => {
            const card = document.createElement("div");
            card.classList.add("col-md-3", "mb-4");
            card.innerHTML = `
            <div class="card h-100">
            <img src="/${produto.imagem}" class="card-img-top" style="height: 259px; object-fit: cover;" alt="Imagem do Produto">
                <div class="card-body text-center">
                    <h5 class="card-title">${produto.nome}</h5>
                    <p class="card-text">R$ ${produto.precoVenda.toFixed(2)}</p>
                <button class="btn btn-dark" onclick='adicionarAoCarrinho(${JSON.stringify(produto)})'>Comprar</button>
                </div>
            </div>
            `;
            container.appendChild(card);
        });

    } catch (error) {
        console.error("Erro ao carregar produtos:", error);
    }
});

function adicionarAoCarrinho(produto) {
    let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];
    carrinho.push({ ...produto, quantidade: 1 });
    localStorage.setItem("carrinho", JSON.stringify(carrinho));
    alert(`Produto "${produto.nome}" adicionado ao carrinho!`);
}