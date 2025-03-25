document.addEventListener("DOMContentLoaded", async function() {
    const urlParams = new URLSearchParams(window.location.search);
    const clienteId = urlParams.get("id");

    if (!clienteId) {
        console.error("ID do cliente não encontrado na URL.");
        return;
    }

    const clienteIdInput = document.getElementById("clienteId");
    if (clienteIdInput) clienteIdInput.value = clienteId;

    const voltarButton = document.getElementById("voltar");
    if (voltarButton) voltarButton.href = `/clientes_editar?id=${clienteId}`;

    try {
        let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}/enderecos`);

        if (!resposta.ok) {
            console.error("Erro ao buscar endereços: ", resposta.statusText);
            return;
        }

        let enderecos = await resposta.json();
        console.log("Endereços recebidos:", enderecos);

        if (!Array.isArray(enderecos) || enderecos.length === 0) {
            console.warn("Nenhum endereço encontrado para o cliente.");
            return;
        }

        const tipos = ["residencial", "cobranca", "entrega"];
        tipos.forEach((tipo, index) => {
            if (enderecos[index]) {
                preencherCamposEndereco(tipo, enderecos[index]);
            }
        });
    } catch (error) {
        console.error("Erro ao buscar endereços:", error);
    }
});


//buscar campos
function preencherCamposEndereco(tipo, endereco) {
    if (!endereco) return;

    document.getElementById(`tipo_endereco_${tipo}`).value = endereco.tipoEndereco;
    document.getElementById(`tipo_residencia_${tipo}`).value = endereco.tipoResidencia;
    document.getElementById(`tipo_logradouro_${tipo}`).value = endereco.tipoLogradouro;
    document.getElementById(`nome_logradouro_${tipo}`).value = endereco.logradouro;
    document.getElementById(`numero_${tipo}`).value = endereco.numero;
    document.getElementById(`cep_${tipo}`).value = endereco.cep;
    document.getElementById(`bairro_${tipo}`).value = endereco.bairro;
    document.getElementById(`cidade_${tipo}`).value = endereco.cidade;
    document.getElementById(`estado_${tipo}`).value = endereco.estado;
    document.getElementById(`pais_${tipo}`).value = endereco.pais;
}

//salvar
async function salvarEnderecos() {
    const clienteId = new URLSearchParams(window.location.search).get("id");
    if (!clienteId) {
        alert("Erro: ID do cliente não encontrado.");
        return;
    }

    const enderecos = ["residencial", "cobranca", "entrega"].map(tipo => ({
        tipoEndereco: document.getElementById(`tipo_endereco_${tipo}`).value,
        tipoResidencia: document.getElementById(`tipo_residencia_${tipo}`).value,
        tipoLogradouro: document.getElementById(`tipo_logradouro_${tipo}`).value,
        logradouro: document.getElementById(`nome_logradouro_${tipo}`).value,
        numero: document.getElementById(`numero_${tipo}`).value,
        cep: document.getElementById(`cep_${tipo}`).value,
        bairro: document.getElementById(`bairro_${tipo}`).value,
        cidade: document.getElementById(`cidade_${tipo}`).value,
        estado: document.getElementById(`estado_${tipo}`).value,
        pais: document.getElementById(`pais_${tipo}`).value
    }));

    try {
        let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}/enderecos`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(enderecos)
        });

        if (resposta.ok) {
            alert("Endereços atualizados com sucesso!");
            window.location.href = `/clientes_editar?id=${clienteId}`;
        } else {
            let errorText = await resposta.text();
            alert("Erro ao atualizar endereços: " + errorText);
        }
    } catch (error) {
        console.error("Erro ao atualizar endereços:", error);
        alert("Erro ao conectar com o servidor.");
    }
}