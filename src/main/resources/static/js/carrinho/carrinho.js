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

function salvarCarrinho(carrinho) {
    const agora = Date.now();
    const carrinhoComDados = carrinho.map(p => ({
        ...p,
        adicionadoEm: p.adicionadoEm || agora,
        expirado: p.expirado || false
    }));
    localStorage.setItem("carrinho", JSON.stringify(carrinhoComDados));
}

function verificarItensExpirados(tempoLimiteMin = 5) {
    let carrinho = carregarCarrinho();
    const agora = Date.now();
    let houveExpirado = false;

    carrinho = carrinho.map(item => {
        const expirado = (agora - item.adicionadoEm) > tempoLimiteMin * 60 * 1000;
        if (expirado && !item.expirado) houveExpirado = true;
        return {
            ...item,
            expirado: expirado
        };
    });

    if (houveExpirado) {
        localStorage.setItem("carrinho", JSON.stringify(carrinho));
        atualizarTabela(); // atualiza visualmente
    }
}

function reAdicionarItemExpirado(id) {
    let carrinho = carregarCarrinho();
    const item = carrinho.find(p => p.id === id);
    if (!item) return;

    item.expirado = false;
    item.adicionadoEm = Date.now();
    salvarCarrinho(carrinho);
    atualizarTabela();
}

async function atualizarQuantidade(id, novaQtd) {
    const carrinho = carregarCarrinho();
    const item = carrinho.find(p => p.id === id);

    if (!item) return;

    novaQtd = parseInt(novaQtd);
    if (isNaN(novaQtd) || novaQtd < 1) {
        alert("Quantidade inválida.");
        return;
    }

    try {
        const resp = await fetch(`http://localhost:8080/api/estoque/produto/${id}`);
        const estoque = await resp.json();
        const totalEstoque = estoque.reduce((soma, e) => soma + e.quantidade, 0);

        if (novaQtd > totalEstoque) {
            alert(`Estoque insuficiente. Máximo disponível: ${totalEstoque}`);
            return;
        }

        item.quantidade = novaQtd;
        salvarCarrinho(carrinho);
        atualizarTabela();
    } catch (err) {
        console.error("Erro ao validar estoque:", err);
        alert("Erro ao validar estoque disponível.");
    }
}

function removerDoCarrinho(id) {
    const novo = carregarCarrinho().filter(p => p.id !== id);
    salvarCarrinho(novo);
    atualizarTabela();
}

//salvar novo endereço entrega
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
    if (!idCliente) return alert("Cliente não identificado.");

    const cartao = {
        cartaoNumero: document.getElementById("cartaoNumero").value,
        cartaoValidade: document.getElementById("cartaoValidade").value,
        cartaoCVV: document.getElementById("cartaoCVV").value,
        nomeCartao: document.getElementById("nomeCartao").value,
        cartaoBandeira: document.getElementById("cartaoBandeira").value,
        cartaoPrincipal: false
    };

    try {
        const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}/cartoes`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(cartao)
        });

        if (resp.ok) {
            alert("Cartão salvo com sucesso!");
            fecharFormCartao();
            await carregarCartoes(); // atualiza os selects
        } else {
            const erro = await resp.text();
            alert("Erro ao salvar cartão: " + erro);
        }
    } catch (e) {
        console.error("Erro ao salvar cartão:", e);
        alert("Erro de comunicação com servidor.");
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
    const idCliente = sessionStorage.getItem("idCliente");
    if (!idCliente) return;

    try {
        const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}/cartoes`);
        if (!resp.ok) throw new Error("Erro ao buscar cartões");

        const cartoes = await resp.json();

        const select1 = document.getElementById("cartao1");
        const select2 = document.getElementById("cartao2");

        select1.innerHTML = '<option value="">Selecione</option>';
        select2.innerHTML = '<option value="">Selecione</option>';

        cartoes.forEach(c => {
            const opt1 = document.createElement("option");
            opt1.value = c.id;
            opt1.textContent = `**** ${c.cartaoNumero.slice(-4)} (${c.nomeCartao})`;
            select1.appendChild(opt1);

            const opt2 = opt1.cloneNode(true);
            select2.appendChild(opt2);
        });

    } catch (e) {
        console.error("Erro ao carregar cartões:", e);
        alert("Erro ao carregar cartões.");
    }
}

// frete e cupons
let ultimoFreteAplicado = 0;
let descontoCupomPromo = 0;
let descontoCupomTroca = 0;

//resumo da compra
function atualizarTabela(frete = null) {
    let carrinho = carregarCarrinho();
    const tbody = document.querySelector("tbody");
    tbody.innerHTML = "";

    let total = 0;
    let carrinhoAtualizado = false;

    carrinho = carrinho.filter(produto => {
        if (!produto || produto.quantidade <= 0 || !produto.precoVenda) {
            carrinhoAtualizado = true;
            return false;
        }
        return true;
    });

    carrinho.forEach(produto => {
        const subtotal = produto.precoVenda * produto.quantidade;
        const expirado = produto.expirado;

        total += expirado ? 0 : subtotal;

        tbody.innerHTML += `
        <tr style="${expirado ? 'opacity: 0.5;' : ''}">
            <td>${produto.nome}${expirado ? ' <span style="color:red;">(Expirado)</span>' : ''}</td>
            <td>R$ ${produto.precoVenda.toFixed(2)}</td>
            <td>
                ${expirado ? '---' : `<input type="number" value="${produto.quantidade}" min="1"
                    onchange="atualizarQuantidade(${produto.id}, this.value)">`}
            </td>
            <td>R$ ${subtotal.toFixed(2)}</td>
            <td>
                ${expirado ? '<button class="btn btn-warning btn-sm" onclick="reAdicionarItemExpirado(' + produto.id + ')">Re-adicionar</button>'
            : '<button class="btn btn-danger btn-sm" onclick="removerDoCarrinho(' + produto.id + ')">Remover</button>'}
            </td>
        </tr>
    `;
    });

    if (carrinhoAtualizado) salvarCarrinho(carrinho);

    const totalFinal = Math.max(total + frete - descontoCupomPromo - descontoCupomTroca, 0);
    ultimoFreteAplicado = frete;

    document.getElementById("freteInfo").textContent = frete > 0
        ? `R$ ${frete.toFixed(2)}`
        : "Selecione um endereço";

    document.getElementById("valorCupomPromo").textContent = `- R$ ${descontoCupomPromo.toFixed(2)}`;
    document.getElementById("valorCupomTroca").textContent = `- R$ ${descontoCupomTroca.toFixed(2)}`;
    document.getElementById("totalCarrinho").textContent = `R$ ${totalFinal.toFixed(2)}`;
}

// eventos
function calcularFreteEAtualizar() {
    const carrinho = carregarCarrinho();
    const totalItens = carrinho.reduce((soma, item) => soma + item.quantidade, 0);
    const frete = totalItens * 7.90; // corrigido
    atualizarTabela(frete);
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

async function enviarPedido() {
    let carrinho = carregarCarrinho();
    const idCliente = sessionStorage.getItem("idCliente");
    const idEndereco = document.getElementById("enderecoEntrega").value;
    const expirados = carregarCarrinho().filter(p => p.expirado);
    if (expirados.length > 0) {
        return alert("Existem itens expirados no carrinho. Re-adicione para continuar.");
    }

    if (!idCliente || !idEndereco || carrinho.length === 0) {
        return alert("Preencha todos os campos obrigatórios.");
    }

    // Validação de estoque
    let atualizouCarrinho = false;
    for (let i = carrinho.length - 1; i >= 0; i--) {
        const item = carrinho[i];
        const resp = await fetch(`http://localhost:8080/api/estoque/produto/${item.id}`);
        const estoque = await resp.json();
        const totalEstoque = estoque.reduce((soma, e) => soma + e.quantidade, 0);

        if (totalEstoque === 0) {
            carrinho.splice(i, 1);
            atualizouCarrinho = true;
            alert(`"${item.nome}" está indisponível e foi removido.`);
        } else if (item.quantidade > totalEstoque) {
            item.quantidade = totalEstoque;
            atualizouCarrinho = true;
            alert(`"${item.nome}" ajustado para ${totalEstoque}.`);
        }
    }

    if (atualizouCarrinho) {
        salvarCarrinho(carrinho);
        atualizarTabela();
        return;
    }

    // Montagem dos cartões
    const cartoes = [];
    const usando2Cartoes = document.getElementById("cartao2Container")?.style.display === "block";

    const totalProdutos = carrinho.reduce((soma, p) => soma + p.precoVenda * p.quantidade, 0);
    const totalItens = carrinho.reduce((soma, p) => soma + p.quantidade, 0);
    const frete = totalItens * 7.90;
    const descontoPromo = document.getElementById("cupomPromocional").value.toUpperCase() === "TESTE" ? 100.0 : 0.0;
    const descontoTroca = 0.0; // ajustar depois
    const totalCarrinho = Math.max(totalProdutos + frete - descontoPromo - descontoTroca, 0);

    if (usando2Cartoes) {
        const id1 = document.getElementById("cartao1")?.value;
        const valor1 = parseFloat(document.getElementById("valorCartao1")?.value || "0");
        const id2 = document.getElementById("cartao2")?.value;
        const valor2 = parseFloat(document.getElementById("valorCartao2")?.value || "0");

        if (!id1 || isNaN(valor1) || valor1 < 10) {
            return alert("O valor do primeiro cartão deve ser maior ou igual a R$ 10,00.");
        }
        if (!id2 || isNaN(valor2) || valor2 < 10) {
            return alert("O valor do segundo cartão deve ser maior ou igual a R$ 10,00.");
        }
        if (id1 === id2) {
            return alert("Selecione cartões diferentes.");
        }

        cartoes.push({ idCartao: parseInt(id1), valor: valor1 });
        cartoes.push({ idCartao: parseInt(id2), valor: valor2 });

        const soma = cartoes.reduce((s, c) => s + c.valor, 0);
        if (Math.abs(soma - totalCarrinho) > 0.01) {
            return alert(`A soma dos cartões (R$ ${soma.toFixed(2)}) deve ser igual ao total do pedido (R$ ${totalCarrinho.toFixed(2)}).`);
        }
    } else {
        const id = document.getElementById("cartao1")?.value;
        if (!id) return alert("Selecione um cartão para pagamento.");
        cartoes.push({ idCartao: parseInt(id), valor: totalCarrinho });
    }

    const venda = {
        idCliente: Number(idCliente),
        idEnderecoEntrega: Number(idEndereco),
        cupomPromocional: document.getElementById("cupomPromocional").value,
        cupomTroca: document.getElementById("cupomTroca").value,
        cartoes: cartoes,
        itens: carrinho.map(p => ({
            idProduto: p.id,
            quantidade: p.quantidade
        }))
    };

    console.log("Enviando venda:", JSON.stringify(venda, null, 2));
    console.log("Total esperado:", totalCarrinho);

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
        const erro = await resposta.text();
        alert("Erro ao finalizar pedido:\n" + erro);
    }
}

// listeners
document.addEventListener("DOMContentLoaded", () => {

    carregarEnderecosEntrega();
    carregarCartoes();
    atualizarTabela();

    let tempoRestante = 5 * 60;

    function atualizarContador() {
        const minutos = Math.floor(tempoRestante / 60);
        const segundos = tempoRestante % 60;
        document.getElementById("contadorCarrinho").textContent = `Tempo restante: ${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`;

        verificarItensExpirados(); // checa se algum expirou

        if (tempoRestante <= 0) {
            clearInterval(contadorInterval);
            document.getElementById("contadorCarrinho").textContent = "Tempo expirado";
        }

        tempoRestante--;
    }

    setInterval(atualizarContador, 1000);

    document.getElementById("enderecoEntrega").addEventListener("change", () => {
        const idEndereco = document.getElementById("enderecoEntrega").value;
        if (idEndereco && idEndereco !== "") {
            calcularFreteEAtualizar();
        } else {
            atualizarTabela(0);
        }
    });

    setTimeout(() => {
        const btn = document.getElementById("btnFinalizarPedido");
        if (btn) {
            btn.addEventListener("click", enviarPedido);
        } else {
            console.warn("Botão finalizar não encontrado no DOM.");
        }
    }, 500);

    document.getElementById("cupomPromocional").addEventListener("blur", () => validarCupom("cupomPromocional", "promo"));
    document.getElementById("cupomTroca").addEventListener("blur", () => validarCupom("cupomTroca", "troca"));
});