document.addEventListener("DOMContentLoaded", async function () {
    const urlParams = new URLSearchParams(window.location.search);
    const clienteId = urlParams.get("id");
    document.getElementById("voltar").href += clienteId;

    if (clienteId) {
        try {
            let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}/cartoes`);
            let cartoes = await resposta.json();

            console.log("Cartões recebidos:", cartoes);

            const container = document.getElementById("cartoesContainer");
            cartoes.forEach(cartao => {
                adicionarCartao(cartao);
            });

        } catch (error) {
            console.error("Erro ao buscar cartões:", error);
        }
    }
});

//adicionar novo cartao
function adicionarCartao(cartao = {}) {
    const container = document.getElementById("cartoesContainer");

    let novoCartao = document.createElement("div");
    novoCartao.classList.add("cartao", "p-3", "border", "rounded", "mt-2");

    novoCartao.innerHTML = `
        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Número do Cartão</label>
                <input type="text" class="form-control cartaoNumero" value="${cartao.cartaoNumero || ""}" required>
            </div>
            <div class="col-md-3 mb-3">
                <label class="form-label">Bandeira</label>
                <select class="form-control cartaoBandeira">
                    <option value="Visa" ${cartao.cartaoBandeira === "Visa" ? "selected" : ""}>Visa</option>
                    <option value="Mastercard" ${cartao.cartaoBandeira === "Mastercard" ? "selected" : ""}>Mastercard</option>
                    <option value="Amex" ${cartao.cartaoBandeira === "Amex" ? "selected" : ""}>American Express</option>
                    <option value="Elo" ${cartao.cartaoBandeira === "Elo" ? "selected" : ""}>Elo</option>
                </select>
            </div>
            <div class="col-md-3 mb-3 d-flex align-items-center">
                <input type="radio" class="cartaoPrincipal me-2" name="cartaoPrincipal" ${cartao.cartaoPrincipal ? "checked" : ""}>
                <label>Cartão Principal</label>
            </div>
        </div>
        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Nome Impresso</label>
                <input type="text" class="form-control nomeCartao" value="${cartao.nomeCartao || ""}" required>
            </div>
            <div class="col-md-3 mb-3">
                <label class="form-label">Validade</label>
                <input type="text" class="form-control cartaoValidade" value="${cartao.cartaoValidade || ""}" required>
            </div>
            <div class="col-md-3 mb-3">
                <label class="form-label">CVV</label>
                <input type="text" class="form-control cartaoCVV" value="${cartao.cartaoCVV || ""}" required>
            </div>
        </div>
        <button type="button" class="btn btn-danger btn-sm" onclick="removerCartao(this)">Remover</button>
    `;

    container.appendChild(novoCartao);
}

//remover cartao
function removerCartao(botao) {
    botao.parentElement.remove();
}

//salvar edições cartao
async function salvarCartoes() {
    const clienteId = new URLSearchParams(window.location.search).get("id");
    const cartoes = [];

    document.querySelectorAll(".cartao").forEach(cartaoDiv => {
        let cartao = {
            cartaoNumero: cartaoDiv.querySelector(".cartaoNumero").value,
            cartaoBandeira: cartaoDiv.querySelector(".cartaoBandeira").value,
            cartaoValidade: cartaoDiv.querySelector(".cartaoValidade").value,
            cartaoCVV: cartaoDiv.querySelector(".cartaoCVV").value,
            nomeCartao: cartaoDiv.querySelector(".nomeCartao").value,
            cartaoPrincipal: cartaoDiv.querySelector(".cartaoPrincipal").checked
        };
        cartoes.push(cartao);
    });

    let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}/cartoes`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cartoes)
    });

    if (resposta.ok) {
        alert("Cartões atualizados com sucesso!");
        window.location.href = `/clientes_editar?id=${clienteId}`;
    } else {
        alert("Erro ao atualizar cartões.");
    }
}