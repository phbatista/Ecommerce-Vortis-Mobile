// cliente via URL
const urlParams = new URLSearchParams(window.location.search);
const idCliente = urlParams.get('idCliente');
console.log("ID do cliente:", idCliente);

// funções carrinho
function carregarCarrinho() {
    return JSON.parse(localStorage.getItem('carrinho')) || [];
}

function salvarCarrinho(carrinho) {
    localStorage.setItem('carrinho', JSON.stringify(carrinho));
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
    let carrinho = carregarCarrinho().filter(p => p.id !== id);
    salvarCarrinho(carrinho);
    atualizarTabela();
}

// carregar endereços do cliente (tipo "Entrega")
async function carregarEnderecosEntrega() {
    try {
        const resposta = await fetch(`http://localhost:8080/api/clientes/${idCliente}`);
        const cliente = await resposta.json();

        console.log("Cliente recebido:", cliente); // debug

        const select = document.getElementById("enderecoEntrega");
        select.innerHTML = '<option value="">Selecione um endereço</option>';

        const enderecosEntrega = cliente.enderecos.filter(e => e.tipoEndereco === "Entrega");

        if (enderecosEntrega.length === 0) {
            select.innerHTML = '<option value="">Nenhum endereço de entrega encontrado</option>';
            return;
        }

        enderecosEntrega.forEach(e => {
            const option = document.createElement("option");
            option.value = e.id;
            option.textContent = `${e.logradouro}, ${e.numero} - ${e.bairro}, ${e.cidade}`;
            select.appendChild(option);
        });
    } catch (err) {
        console.error("Erro ao carregar endereços de entrega:", err);
    }
}

let ultimoFreteCalculado = 0; // 👈 guardar o frete atual

function atualizarTabela(frete = 0) {
    const carrinho = carregarCarrinho();
    const tbody = document.querySelector("tbody");
    const totalSpan = document.getElementById("totalCarrinho");
    const freteSpan = document.getElementById("valorFreteResumo");
    tbody.innerHTML = "";

    let total = 0;

    carrinho.forEach(produto => {
        const subtotal = produto.precoVenda * produto.quantidade;
        total += subtotal;

        const row = `
        <tr>
            <td>${produto.nome}</td>
            <td>R$ ${produto.precoVenda.toFixed(2)}</td>
            <td>
                <input type="number" value="${produto.quantidade}" min="1" onchange="atualizarQuantidade(${produto.id}, this.value)">
            </td>
            <td>R$ ${subtotal.toFixed(2)}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="removerDoCarrinho(${produto.id})">Remover</button>
            </td>
        </tr>`;
        tbody.innerHTML += row;
    });

    ultimoFreteCalculado = frete;
    freteSpan.textContent = `R$ ${frete.toFixed(2)}`;
    totalSpan.textContent = `R$ ${(total + frete).toFixed(2)}`;
}


document.addEventListener("DOMContentLoaded", () => {
    // carregar endereços + tabela
    carregarEnderecosEntrega();
    atualizarTabela();

    // só agora adiciona o evento ao botão
    document.getElementById("btnCalcularFrete").addEventListener("click", async () => {
        const idEndereco = document.getElementById("enderecoEntrega").value;
        const carrinho = carregarCarrinho();

        if (!idEndereco) {
            alert("Selecione um endereço para calcular o frete.");
            return;
        }

        if (!idCliente) {
            alert("Cliente não identificado.");
            return;
        }

        try {
            const resp = await fetch(`http://localhost:8080/api/clientes/${idCliente}`);
            const cliente = await resp.json();

            const endereco = cliente.enderecos.find(e => e.id == idEndereco);
            const cep = endereco.cep;

            const dto = {
                cepEntrega: cep,
                itens: carrinho.map(p => ({
                    idProduto: p.id,
                    quantidade: p.quantidade
                }))
            };

            const freteResp = await fetch("http://localhost:8080/api/frete/calcular", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            if (freteResp.ok) {
                const frete = await freteResp.json();
                document.getElementById("freteInfo").textContent = `Frete: R$ ${frete.toFixed(2)}`;
                atualizarTabela(frete);
            }
        } catch (err) {
            console.error("Erro ao calcular frete:", err);
            alert("Erro ao calcular o frete.");
        }
    });

// finalizar pedido
    document.getElementById("btnFinalizarPedido").addEventListener("click", async () => {
        const carrinho = carregarCarrinho();
        const idEndereco = document.getElementById("enderecoEntrega").value;

        if (!idEndereco) {
            alert("Selecione um endereço de entrega.");
            return;
        }

        if (carrinho.length === 0) {
            alert("Seu carrinho está vazio.");
            return;
        }

        const venda = {
            idCliente: idCliente,
            idEnderecoEntrega: Number(idEndereco),
            itens: carrinho.map(p => ({
                idProduto: p.id,
                quantidade: p.quantidade
            }))
        };

        try {
            const resposta = await fetch("http://localhost:8080/api/vendas", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(venda)
            });

            if (resposta.ok) {
                alert("Pedido finalizado com sucesso!");
                localStorage.removeItem("carrinho");
                window.location.href = "/pedidos";
            } else {
                alert("Erro ao finalizar pedido.");
            }
        } catch (e) {
            console.error(e);
            alert("Erro de comunicação com o servidor.");
        }
    });
});

function mostrarFormularioEndereco() {
    document.getElementById("formNovoEndereco").style.display = "block";
}

function fecharFormularioEndereco() {
    document.getElementById("formNovoEndereco").style.display = "none";
}

// salvar endereço no banco
async function salvarNovoEndereco() {
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
        pais: document.getElementById("pais_entrega").value,
        cliente: { id: idCliente } // necessário se o relacionamento for @ManyToOne
    };

    try {
        const resp = await fetch("http://localhost:8080/api/enderecos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(endereco)
        });

        if (resp.ok) {
            alert("Endereço cadastrado com sucesso!");
            fecharFormularioEndereco();
            await carregarEnderecosEntrega(); // recarrega o select

            const novoEndereco = await resp.json(); // recupera ID
            document.getElementById("enderecoEntrega").value = novoEndereco.id;
        } else {
            alert("Erro ao cadastrar endereço.");
        }
    } catch (e) {
        console.error(e);
        alert("Erro ao cadastrar endereço.");
    }
}