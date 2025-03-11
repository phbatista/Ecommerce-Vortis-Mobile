document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const clienteId = urlParams.get("id");

    if (clienteId) {
        document.getElementById("voltar").href = `/clientes_editar?id=${clienteId}`;
    }
});

async function alterarSenha() {
    const urlParams = new URLSearchParams(window.location.search);
    const clienteId = urlParams.get("id");

    const senhaAtual = document.getElementById("senhaAtual").value;
    const novaSenha = document.getElementById("novaSenha").value;
    const confirmarSenha = document.getElementById("confirmarSenha").value;

    if (novaSenha !== confirmarSenha) {
        document.getElementById("erroSenha").style.display = "block";
        return;
    }

    let resposta = await fetch(`http://localhost:8080/api/clientes/${clienteId}/alterar-senha`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ senhaAtual, novaSenha })
    });

    let resultado = await resposta.text();

    if (resposta.ok) {
        alert("Senha alterada com sucesso!");
        window.location.href = `/clientes_editar?id=${clienteId}`;
    } else {
        alert("Erro ao alterar senha: " + resultado);
    }
}