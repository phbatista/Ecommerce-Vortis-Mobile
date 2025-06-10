document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get("id");

    const resp = await fetch(`http://localhost:8080/api/produtos/${id}`);
    const produto = await resp.json();

    document.getElementById("id").value = produto.id;
    document.getElementById("nome").value = produto.nome;
    document.getElementById("disponibilidade_ano").value = produto.disponibilidadeAno;
    document.getElementById("chipset").value = produto.chipset;
    document.getElementById("sistema_operacional").value = produto.sistemaOperacional;
    document.getElementById("bateria").value = produto.bateria;
    document.getElementById("resolucao_camera").value = produto.resolucaoCamera;
    document.getElementById("gpu").value = produto.gpu;
    document.getElementById("tipo_tela").value = produto.tipoTela;
    document.getElementById("tamanho_tela").value = produto.tamanhoTela;
    document.getElementById("peso").value = produto.peso;
    document.getElementById("altura").value = produto.altura;
    document.getElementById("largura").value = produto.largura;
    document.getElementById("profundidade").value = produto.profundidade;
    document.getElementById("memoria_ram").value = produto.memoriaRam;
    document.getElementById("armazenamento").value = produto.armazenamento;
    document.getElementById("codigo_barras").value = produto.codigoBarras;
    document.getElementById("grupo_precificacao").value = produto.grupoPrecificacao;

    if (produto.categorias && Array.isArray(produto.categorias)) {
        const nomesCategorias = produto.categorias.map(cat => cat.nome);
        document.querySelectorAll("input[name='categorias']").forEach(cb => {
            cb.checked = nomesCategorias.includes(cb.value);
        });
    }

    document.getElementById("formProduto").addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData();
        const id = document.getElementById("id").value;

        formData.append("nome", document.getElementById("nome").value);
        formData.append("disponibilidadeAno", document.getElementById("disponibilidade_ano").value);
        formData.append("chipset", document.getElementById("chipset").value);
        formData.append("sistemaOperacional", document.getElementById("sistema_operacional").value);
        formData.append("bateria", document.getElementById("bateria").value);
        formData.append("resolucaoCamera", document.getElementById("resolucao_camera").value);
        formData.append("gpu", document.getElementById("gpu").value);
        formData.append("tipoTela", document.getElementById("tipo_tela").value);
        formData.append("tamanhoTela", document.getElementById("tamanho_tela").value);
        formData.append("peso", document.getElementById("peso").value);
        formData.append("altura", document.getElementById("altura").value);
        formData.append("largura", document.getElementById("largura").value);
        formData.append("profundidade", document.getElementById("profundidade").value);
        formData.append("memoriaRam", document.getElementById("memoria_ram").value);
        formData.append("armazenamento", document.getElementById("armazenamento").value);
        formData.append("codigoBarras", document.getElementById("codigo_barras").value);
        formData.append("grupoPrecificacao", document.getElementById("grupo_precificacao").value);
        formData.append("status", "ATIVO");
        formData.append("motivo", "EDIÇÃO");
        formData.append("justificativa", "Edição de Cadastro");

        //CATEGORIAS
        document.querySelectorAll("input[name='categorias']:checked").forEach(cb => {
            formData.append("nomesCategorias", cb.value);
        });

        //IMAGEM
        const imagem = document.getElementById("imagem").files[0];
        if (imagem) {
            formData.append("imagem", imagem);
        }

        // Envio
        const resposta = await fetch(`http://localhost:8080/api/produtos/${id}`, {
            method: "PUT",
            body: formData
        });

        if (resposta.ok) {
            alert("Produto atualizado com sucesso!");
            window.location.href = "/produtos_lista";
        } else {
            const erro = await resposta.text();
            alert("Erro ao atualizar produto: " + erro);
        }
    });
});