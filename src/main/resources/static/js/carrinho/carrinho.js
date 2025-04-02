// cliente via sessionStorage
const idCliente = sessionStorage.getItem("idCliente");
if (!idCliente) {
    alert("Você precisa estar logado para acessar o carrinho.");
    window.location.href = "/clientes_login";
}

// carrinho
function carregarCarrinho() {
    return JSON.parse(localStorage.getItem("carrinho")) || [];
}

function adicionarAoCarrinho(produto) {
    let carrinho = JSON.parse(localStorage.getItem("carrinho")) || [];

    const itemExistente = carrinho.find(p => p.id === produto.id);

    if (itemExistente) {
        itemExistente.quantidade += 1;
    } else {
        carrinho.push({ ...produto, quantidade: 1 });
    }

    localStorage.setItem("carrinho", JSON.stringify(carrinho));
    alert("Produto adicionado ao carrinho!");
}

function salvarCarrinho(carrinho) {
    localStorage.setItem("carrinho", JSON.stringify(carrinho));
}

function atualizarQuantidade(id, novaQtd) {
    const carrinho = carregarCarrinho();
    const item = carrinho.find(p => p.id === id);
    if (item) {
        item.quantidade = parseInt(novaQtd);
        salvarCarrinho(carrinho);
        atualizarTabela();
    }
}
function removerDoCarrinho(id) {
    const novo = carregarCarrinho().filter(p => p.id !== id);
    salvarCarrinho(novo);
    atualizarTabela();
}

//salvar endereço
async function salvarNovoEndereco() {
    const idCliente = sessionStorage.getItem("idCliente");
    if (!idCliente) {
        alert("Cliente não identificado.");
        return;
    }

    const endereco = {
        tipoEndereco: "Entrega",
        tipoResidencia: document.getElementById("tipo_residencia_entrega").value,
        tipoLogradouro: document.getElementById("tipo_logradouro_entrega").value,
        logradouro: document.getElementById("nome_logradouro_entrega").value,
        numero: document.getElementById("numero_entrega").value,
        cep: document.getElementById("cep_entrega").value,
        bairro: document.getElementById("bairro_entrega").value,
        cidade: document.getElementById("cidade_entrega").value,
        estado: document.getElementById("estado_entrega").value,
        pais: document.getElementById("pais_entrega").value
    };

    try {
        const resposta = await fetch(`http://localhost:8080/api/clientes/${idCliente}/enderecos`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(endereco)
        });

        if (resposta.ok) {
            const novo = await resposta.json();
            alert("Endereço cadastrado com sucesso!");
            fecharFormularioEndereco();
            await carregarEnderecosEntrega();
            document.getElementById("enderecoEntrega").value = novo.id;
        } else {
            const erro = await resposta.text();
            alert("Erro ao cadastrar endereço: " + erro);
        }
    } catch (e) {
        console.error("Erro ao cadastrar endereço:", e);
        alert("Erro de comunicação com o servidor.");
    }
}

//novo cartao
async function salvarNovoCartao() {
    const idCliente = sessionStorage.getItem("idCliente");
    if (!idCliente) {
        alert("Cliente não identificado.");
        return;
    }

    const cartao = {
        cartaoNumero: document.getElementById("cartaoNumero").value,
        cartaoValidade: document.getElementById("cartaoValidade").value,
        cartaoCVV: document.getElementById("cartaoCVV").value,
        nomeCartao: document.getElementById("nomeCartao").value,
        cartaoBandeira: document.getElementById("cartaoBandeira").value,
        cartaoPrincipal: "false"
    };

    try {
        const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}/cartoes`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cartao)
        });

        if (resp.ok) {
            const novoCartao = await resp.json();
            alert("Cartão cadastrado com sucesso!");
            fecharFormCartao();
            await carregarCartoes();
            document.getElementById("cartaoSelecionado").value = novoCartao.id;
        } else {
            const erro = await resp.text();
            alert("Erro ao cadastrar cartão: " + erro);
        }
    } catch (e) {
        console.error("Erro ao cadastrar cartão:", e);
        alert("Erro de comunicação com o servidor.");
    }
}

// dados cliente
async function carregarEnderecosEntrega() {
    const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}`);
    const cliente = await resp.json();
    const select = document.getElementById("enderecoEntrega");
    select.innerHTML = '<option value="">Selecione um endereço</option>';
    cliente.enderecos.filter(e => e.tipoEndereco === "Entrega").forEach(e => {
        const opt = document.createElement("option");
        opt.value = e.id;
        opt.textContent = `${e.logradouro}, ${e.numero} - ${e.bairro}, ${e.cidade}`;
        select.appendChild(opt);
    });
}
async function carregarCartoes() {
    const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}/cartoes`);
    const cartoes = await resp.json();
    const select = document.getElementById("cartaoSelecionado");
    select.innerHTML = '<option value="">Selecione um cartão</option>';
    cartoes.forEach(c => {
        const opt = document.createElement("option");
        opt.value = c.id;
        opt.textContent = `**** ${c.cartaoNumero.slice(-4)} (${c.nomeCartao})`;
        select.appendChild(opt);
    });
}

// frete e cupons
let ultimoFreteAplicado = 0;
let descontoCupomPromo = 0;
let descontoCupomTroca = 0;

function atualizarTabela(frete = 0) {
    const carrinho = carregarCarrinho();
    const tbody = document.querySelector("tbody");
    tbody.innerHTML = "";
    let total = 0;
    carrinho.forEach(produto => {
        const subtotal = produto.precoVenda * produto.quantidade;
        total += subtotal;
        tbody.innerHTML += `
        <tr>
            <td>${produto.nome}</td>
            <td>R$ ${produto.precoVenda.toFixed(2)}</td>
            <td><input type="number" value="${produto.quantidade}" min="1" onchange="atualizarQuantidade(${produto.id}, this.value)"></td>
            <td>R$ ${subtotal.toFixed(2)}</td>
            <td><button class="btn btn-danger btn-sm" onclick="removerDoCarrinho(${produto.id})">Remover</button></td>
        </tr>`;
    });
    const totalFinal = Math.max(total + frete - descontoCupomPromo - descontoCupomTroca, 0);
    ultimoFreteAplicado = frete;
    document.getElementById("freteInfo").textContent = `R$ ${frete.toFixed(2)}`;
    document.getElementById("totalCarrinho").textContent = `R$ ${totalFinal.toFixed(2)}`;
}

// eventos
async function calcularFrete() {
    const idEndereco = document.getElementById("enderecoEntrega").value;
    if (!idEndereco) return alert("Selecione um endereço");
    const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}`);
    const cliente = await resp.json();
    const endereco = cliente.enderecos.find(e => e.id == idEndereco);
    const dto = {
        cepEntrega: endereco.cep,
        itens: carregarCarrinho().map(p => ({ idProduto: p.id, quantidade: p.quantidade }))
    };
    const freteResp = await fetch("http://localhost:8080/api/frete/calcular", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    });
    if (freteResp.ok) {
        const valor = await freteResp.json();
        atualizarTabela(valor);
    }
}

async function validarCupom(campoId, tipo) {
    const codigo = document.getElementById(campoId).value;
    if (!codigo) return;
    try {
        const resp = await fetch(`http://localhost:8080/api/cupons/validar/${codigo}?idCliente=${idCliente}`);
        if (resp.ok) {
            const cupom = await resp.json();
            if (tipo === 'promo') descontoCupomPromo = cupom.valor;
            if (tipo === 'troca') descontoCupomTroca = cupom.valor;
            alert(`Cupom aplicado: R$ ${cupom.valor.toFixed(2)}`);
        } else {
            if (tipo === 'promo') descontoCupomPromo = 0;
            if (tipo === 'troca') descontoCupomTroca = 0;
            alert("Cupom inválido");
        }
        atualizarTabela(ultimoFreteAplicado);
    } catch (e) {
        alert("Erro ao validar cupom");
    }
}

// pedido
async function finalizarPedido() {
    const carrinho = carregarCarrinho();
    const idEndereco = document.getElementById("enderecoEntrega").value;
    const idCartao = document.getElementById("cartaoSelecionado").value;
    if (!idEndereco || !idCartao || carrinho.length === 0) return alert("Preencha todos os campos");
    const venda = {
        idCliente: idCliente,
        idEnderecoEntrega: Number(idEndereco),
        idCartao: Number(idCartao),
        cupomPromocional: document.getElementById("cupomPromocional").value,
        cupomTroca: document.getElementById("cupomTroca").value,
        itens: carrinho.map(p => ({ idProduto: p.id, quantidade: p.quantidade }))
    };
    const resposta = await fetch("http://localhost:8080/api/vendas", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(venda)
    });

    if (resposta.ok) {
        const idVenda = await resposta.json();
        localStorage.removeItem("carrinho");

        Swal.fire({
            title: 'Pedido Confirmado!',
            html: `Seu pedido foi realizado com sucesso.<br><strong>ID do Pedido:</strong> ${idVenda}`,
            icon: 'success',
            confirmButtonText: 'OK',
            confirmButtonColor: '#198754'
        }).then(() => {
            window.location.href = "/catalogo";
        });
    } else {
        alert("Erro ao finalizar pedido.");
    }
}



// listeners
document.addEventListener("DOMContentLoaded", () => {
    carregarEnderecosEntrega();
    carregarCartoes();
    atualizarTabela();
    document.getElementById("btnCalcularFrete").addEventListener("click", calcularFrete);
    document.getElementById("btnFinalizarPedido").addEventListener("click", finalizarPedido);
    document.getElementById("cupomPromocional").addEventListener("blur", () => validarCupom("cupomPromocional", "promo"));
    document.getElementById("cupomTroca").addEventListener("blur", () => validarCupom("cupomTroca", "troca"));
});