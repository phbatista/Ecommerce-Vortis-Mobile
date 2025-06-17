document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formHistorico");

    if (form) {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            await carregarGrafico();
        });
    }
});

async function carregarGrafico() {
    const inicio = document.getElementById("inicio").value;
    const fim = document.getElementById("fim").value;
    const tipo = document.getElementById("tipoAgrupamento").value;

    const resp = await fetch(`http://localhost:8080/api/vendas/admin/historico-vendas?inicio=${inicio}&fim=${fim}&tipo=${tipo}`);
    const dados = await resp.json();

    // Agrupar por data + produto/categoria
    const agrupado = {};
    dados.forEach(d => {
        const chave = `${d.dataVenda}|${d.nomeProdutoOuCategoria}`;
        agrupado[chave] = (agrupado[chave] || 0) + d.quantidadeVendida;
    });

    // Reorganiza para múltiplas séries
    const porNome = {};
    Object.keys(agrupado).forEach(chave => {
        const [data, nome] = chave.split("|");
        if (!porNome[nome]) porNome[nome] = {};
        porNome[nome][data] = agrupado[chave];
    });

    const labels = Array.from(new Set(dados.map(d => d.dataVenda))).sort();

    const datasets = Object.keys(porNome).map(nome => ({
        label: nome,
        data: labels.map(data => porNome[nome][data] || 0),
        borderWidth: 2
    }));

    const ctx = document.getElementById("graficoVendas").getContext("2d");
    if (window.grafico) window.grafico.destroy();

    window.grafico = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    ticks: {
                        precision: 0 // força números inteiros
                    },
                    beginAtZero: true
                }
            },
            plugins: {
                legend: { display: true },
                title: {
                    display: true,
                    text: `Histórico de vendas por ${tipo}`
                }
            }
        }
    });
}