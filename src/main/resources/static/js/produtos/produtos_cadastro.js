document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("formProduto").addEventListener("submit", async function (event) {
        event.preventDefault();

        const form = document.getElementById("formProduto");
        const formData = new FormData();

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
        formData.append("motivo", "CADASTRO");
        formData.append("justificativa", "Novo Cadastro");

        //categorias
        const categoriasSelecionadas = Array.from(document.querySelectorAll("input[name='categorias']:checked"))
            .map(cb => cb.value);
        categoriasSelecionadas.forEach(cat => formData.append("nomesCategorias", cat));

        //imagem
        const imagemInput = document.getElementById("imagem");
        formData.append("imagem", imagemInput.files[0]);

        try {
            const resposta = await fetch("http://localhost:8080/api/produtos", {
                method: "POST",
                body: formData
            });

            if (resposta.ok) {
                alert("Produto cadastrado com sucesso!");
                document.getElementById("formProduto").reset();
            } else {
                const erro = await resposta.text();
                alert("Erro ao cadastrar produto: " + erro);
            }
        } catch (error) {
            console.error("Erro ao conectar com o servidor:", error);
            alert("Erro na requisição.");
        }
    });
});