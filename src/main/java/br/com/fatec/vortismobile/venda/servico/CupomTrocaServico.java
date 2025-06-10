package br.com.fatec.vortismobile.venda.servico;

import br.com.fatec.vortismobile.notificacao.servico.NotificacaoServico;
import br.com.fatec.vortismobile.venda.modelo.CupomTroca;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.repositorio.CupomTrocaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CupomTrocaServico {

    @Autowired
    private NotificacaoServico notificacaoServico;

    @Autowired
    private CupomTrocaRepositorio cupomTrocaRepositorio;

    public void gerarCupomParaVenda(Venda venda) {
        CupomTroca cupom = new CupomTroca();
        cupom.setCliente(venda.getCliente());
        String codigo = "TROCA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        cupom.setCodigo(codigo);

        double valorTotal = venda.getItens().stream()
                .mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade())
                .sum();

        cupom.setValor(BigDecimal.valueOf(valorTotal));
        cupom.setUsado(false);

        System.out.println("Cupom gerado: " + codigo + " | Valor estimado: R$ " + valorTotal);

        cupomTrocaRepositorio.save(cupom);
        notificacaoServico.gerarNotificacaoCupomGerado(cupom);
    }
}