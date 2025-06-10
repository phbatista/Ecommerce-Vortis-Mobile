package br.com.fatec.vortismobile.venda.servico;

import br.com.fatec.vortismobile.notificacao.servico.NotificacaoServico;
import br.com.fatec.vortismobile.venda.modelo.CupomTroca;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.modelo.VendaCartao;
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

        // Usa o valor efetivamente pago (soma dos cartões)
        double totalPago = venda.getCartoes().stream()
                .mapToDouble(VendaCartao::getValor)
                .sum();

        BigDecimal valorCupom = BigDecimal.valueOf(totalPago)
                .subtract(BigDecimal.valueOf(venda.getFrete()))
                .max(BigDecimal.ZERO);

        cupom.setValor(valorCupom);
        cupom.setUsado(false);

        System.out.println("Cupom gerado: " + codigo + " | Valor final: R$ " + valorCupom);

        cupomTrocaRepositorio.save(cupom);
        notificacaoServico.gerarNotificacaoCupomGerado(cupom);
    }
}