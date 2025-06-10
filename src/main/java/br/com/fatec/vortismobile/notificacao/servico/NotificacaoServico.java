package br.com.fatec.vortismobile.notificacao.servico;

import br.com.fatec.vortismobile.notificacao.dto.NotificacaoDTO;
import br.com.fatec.vortismobile.notificacao.modelo.Notificacao;
import br.com.fatec.vortismobile.notificacao.repositorio.NotificacaoRepositorio;
import br.com.fatec.vortismobile.venda.modelo.CupomTroca;
import br.com.fatec.vortismobile.venda.modelo.TrocaDevolucao;
import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacaoServico {

    @Autowired
    private NotificacaoRepositorio notificacaoRepositorio;

    public void gerarNotificacaoCompraRealizada(Cliente cliente, Long idVenda) {
        Notificacao n = new Notificacao();
        n.setCliente(cliente);
        n.setData(LocalDateTime.now());
        n.setTipo("Compra Realizada");
        n.setMensagem("Sua compra #" + idVenda + " foi realizada com sucesso!");
        notificacaoRepositorio.save(n);
    }

    public void gerarNotificacaoTrocaSolicitada(Cliente cliente, TrocaDevolucao troca) {
        Notificacao n = new Notificacao();
        n.setCliente(cliente);
        n.setData(LocalDateTime.now());
        n.setMensagem("Solicitação de " + troca.getTipo().toLowerCase() + " registrada para o pedido #" + troca.getVenda().getId());
        n.setTipo("Solicitação de Troca/Devolução");
        notificacaoRepositorio.save(n);
    }

    public void gerarNotificacaoStatusAtualizado(Cliente cliente, Long idPedido, String novoStatus) {
        Notificacao n = new Notificacao();
        n.setCliente(cliente);
        n.setData(LocalDateTime.now());
        n.setMensagem("O status do seu pedido #" + idPedido + " foi atualizado para: " + novoStatus);
        n.setTipo("Atualização de Pedido");
        notificacaoRepositorio.save(n);
    }

    public void gerarNotificacaoCupomGerado(CupomTroca cupom) {
        Notificacao n = new Notificacao();
        n.setCliente(cupom.getCliente());
        n.setData(LocalDateTime.now());
        n.setMensagem("Cupom de troca gerado: " + cupom.getCodigo() + " (R$ " + cupom.getValor() + ")");
        n.setTipo("Cupom de Troca");
        notificacaoRepositorio.save(n);
    }

    public List<NotificacaoDTO> listarPorCliente(Long idCliente) {
        List<Notificacao> lista = notificacaoRepositorio.findByClienteId(idCliente);

        return lista.stream()
                .map(n -> new NotificacaoDTO(n.getTipo(), n.getMensagem(), n.getData()))
                .collect(Collectors.toList());
    }
}