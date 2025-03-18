document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("clienteForm").addEventListener("submit", async function (event) {
        event.preventDefault();

        let cliente = {
            nome: document.getElementById("nome").value,
            dataNascimento: document.getElementById("dataNascimento").value,
            cpf: document.getElementById("cpf").value,
            genero: document.getElementById("genero").value,
            email: document.getElementById("email").value,
            senha: document.getElementById("senha").value,
            status: document.getElementById("status").value,
            telefone: {
                tipo: document.getElementById("tipoTelefone").value,
                ddd: document.getElementById("ddd").value,
                numero: document.getElementById("telefone").value
            },
            enderecos: [
                capturarEndereco("residencial"),
                capturarEndereco("cobranca"),
                capturarEndereco("entrega")
            ],
            cartoes: capturarCartoes()
        };

        console.log("Enviando dados para o backend:", JSON.stringify(cliente, null, 2));

        try {
            let resposta = await fetch("http://localhost:8080/api/clientes", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(cliente)
            });

            let resultado = await resposta.json();
            console.log(resultado);

            if (resposta.ok) {
                alert("Cliente cadastrado com sucesso!");
                document.getElementById("clienteForm").reset();

                setTimeout(() => {
                    let mensagemSucesso = document.getElementById("mensagem-sucesso");
                    mensagemSucesso.style.display = "block";
                    mensagemSucesso.innerText = "Cadastro realizado com sucesso!";
                }, 2000);

            } else {
                alert("Erro ao cadastrar cliente: " + (resultado.erro || "Erro desconhecido"));
            }

        } catch (error) {
            console.error("Erro ao enviar requisição:", error);
            alert("Erro ao conectar com o servidor.");
        }
    });
});


function capturarEndereco(tipo) {
    return {
        tipoEndereco: document.getElementById(`tipo_endereco_${tipo}`)?.value || "",
        tipoResidencia: document.getElementById(`tipo_residencia_${tipo}`)?.value || "",
        tipoLogradouro: document.getElementById(`tipo_logradouro_${tipo}`)?.value || "",
        logradouro: document.getElementById(`nome_logradouro_${tipo}`)?.value || "",
        numero: document.getElementById(`numero_${tipo}`)?.value || "",
        cep: document.getElementById(`cep_${tipo}`)?.value || "",
        bairro: document.getElementById(`bairro_${tipo}`)?.value || "",
        cidade: document.getElementById(`cidade_${tipo}`)?.value || "",
        estado: document.getElementById(`estado_${tipo}`)?.value || "",
        pais: document.getElementById(`pais_${tipo}`)?.value || ""
    };
}

function capturarCartoes() {
    let cartoes = [];
    document.querySelectorAll(".cartao").forEach(cartaoDiv => {
        let cartao = {
            cartaoBandeira: cartaoDiv.querySelector(".cartaoBandeira")?.value || "",
            cartaoNumero: cartaoDiv.querySelector(".cartaoNumero")?.value || "",
            cartaoValidade: cartaoDiv.querySelector(".cartaoValidade")?.value || "",
            cartaoCVV: cartaoDiv.querySelector(".cartaoCVV")?.value || "",
            nomeCartao: cartaoDiv.querySelector(".nomeCartao")?.value || "",
            cartaoPrincipal: cartaoDiv.querySelector(".cartaoPrincipal")?.checked || false
        };
        cartoes.push(cartao);
    });
    return cartoes;
}