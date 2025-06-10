package br.com.fatec.vortismobile.venda.servico;

import br.com.fatec.vortismobile.estoque.servico.EstoqueServico;
import br.com.fatec.vortismobile.notificacao.repositorio.NotificacaoRepositorio;
import br.com.fatec.vortismobile.notificacao.servico.NotificacaoServico;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import br.com.fatec.vortismobile.venda.dto.SolicitacaoTrocaDevolucaoDTO;
import br.com.fatec.vortismobile.venda.dto.TrocaAdminDTO;
import br.com.fatec.vortismobile.venda.dto.TrocaClienteDTO;
import br.com.fatec.vortismobile.venda.modelo.ItemTrocaDevolucao;
import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import br.com.fatec.vortismobile.venda.modelo.TrocaDevolucao;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.repositorio.ItemTrocaDevolucaoRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.ItemVendaRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.TrocaDevolucaoRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.VendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrocaDevolucaoServico {

    @Autowired
    private NotificacaoServico notificacaoServico;

    @Autowired
    private EstoqueServico estoqueServico;

    @Autowired
    private VendaRepositorio vendaRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio;

    @Autowired
    private TrocaDevolucaoRepositorio trocaDevolucaoRepositorio;

    @Autowired
    private ItemTrocaDevolucaoRepositorio itemTrocaDevolucaoRepositorio;


    public void solicitarTrocaOuDevolucao(SolicitacaoTrocaDevolucaoDTO dto) {

        System.out.println("DTO recebido:");
        System.out.println("Pedido: " + dto.getIdPedido());
        System.out.println("Tipo: " + dto.getTipo());
        dto.getItens().forEach(i ->
                System.out.println("Produto: " + i.getIdProduto() + ", Qtd: " + i.getQuantidade() + ", Motivo: " + i.getMotivo())
        );

        Venda venda = vendaRepositorio.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        if (!"ENTREGUE".equalsIgnoreCase(venda.getStatus())) {
            throw new RuntimeException("Só é possível solicitar troca ou devolução de pedidos com status ENTREGUE.");
        }

        var itensVenda = itemVendaRepositorio.findByVenda(venda);

        TrocaDevolucao trocaDevolucao = new TrocaDevolucao();
        trocaDevolucao.setVenda(venda);
        trocaDevolucao.setTipo(dto.getTipo().toUpperCase());
        trocaDevolucao.setStatus(dto.getTipo().equalsIgnoreCase("TROCA")
                ? "TROCA SOLICITADA" : "DEVOLUÇÃO SOLICITADA");

        trocaDevolucao = trocaDevolucaoRepositorio.save(trocaDevolucao);

        for (SolicitacaoTrocaDevolucaoDTO.ItemTrocaDevolucaoDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDTO.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

            ItemVenda itemVenda = itensVenda.stream()
                    .filter(i -> i.getProduto().getId().equals(produto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado no pedido."));

            if (itemDTO.getQuantidade() <= 0 || itemDTO.getQuantidade() > itemVenda.getQuantidade()) {
                throw new RuntimeException("Quantidade inválida para produto " + produto.getNome());
            }

            ItemTrocaDevolucao itemTroca = new ItemTrocaDevolucao();
            itemTroca.setProduto(produto);
            itemTroca.setQuantidade(itemDTO.getQuantidade());
            itemTroca.setMotivo(itemDTO.getMotivo());
            itemTroca.setTrocaDevolucao(trocaDevolucao);

            itemTrocaDevolucaoRepositorio.save(itemTroca);
        }

        // Atualiza status geral da venda
        venda.setStatus(trocaDevolucao.getStatus());
        vendaRepositorio.save(venda);

        notificacaoServico.gerarNotificacaoTrocaSolicitada(venda.getCliente(), trocaDevolucao);

    }

    public List<TrocaDevolucao> listarTodasAsTrocas() {
        return trocaDevolucaoRepositorio.findAll();
    }

    @Autowired
    private CupomTrocaServico cupomTrocaServico;

    public void confirmarRecebimento(Long idTroca, boolean retornarEstoque) {
        TrocaDevolucao troca = trocaDevolucaoRepositorio.findById(idTroca)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada."));

        if (!troca.getStatus().equals("TROCA SOLICITADA") && !troca.getStatus().equals("DEVOLUÇÃO SOLICITADA")) {
            throw new RuntimeException("Solicitação já foi processada.");
        }

        if (retornarEstoque) {
            for (ItemTrocaDevolucao item : troca.getItens()) {
                Produto produto = item.getProduto();
                estoqueServico.reporPorTroca(produto, item.getQuantidade());
                produtoRepositorio.save(produto);
            }
        }

        troca.setStatus(troca.getTipo().equals("TROCA") ? "TROCA CONCLUÍDA" : "DEVOLUÇÃO CONCLUÍDA");
        trocaDevolucaoRepositorio.save(troca);

        // Gerar cupom de troca
        if (troca.getTipo().equals("TROCA")) {
            cupomTrocaServico.gerarCupomParaVenda(troca.getVenda());
        }
    }

    public List<TrocaClienteDTO> listarPorCliente(Long idCliente) {
        List<TrocaDevolucao> trocas = trocaDevolucaoRepositorio.findAll().stream()
                .filter(t -> t.getVenda().getCliente().getId().equals(idCliente))
                .toList();

        List<TrocaClienteDTO> resultado = new ArrayList<>();

        for (TrocaDevolucao t : trocas) {
            TrocaClienteDTO dto = new TrocaClienteDTO();
            dto.setId(t.getId());
            dto.setIdPedido(t.getVenda().getId());
            dto.setTipo(t.getTipo());
            dto.setStatus(t.getStatus());
            dto.setDataSolicitacao(t.getDataSolicitacao());

            List<TrocaClienteDTO.ItemDTO> itens = new ArrayList<>();
            for (ItemTrocaDevolucao item : t.getItens()) {
                TrocaClienteDTO.ItemDTO i = new TrocaClienteDTO.ItemDTO();
                i.setNome(item.getProduto().getNome());
                i.setQuantidade(item.getQuantidade());
                itens.add(i);
            }

            dto.setItens(itens);
            resultado.add(dto);
        }

        return resultado;
    }

    public List<TrocaAdminDTO> listarTodasParaAdmin() {
        return trocaDevolucaoRepositorio.findAll().stream().map(t -> {
            TrocaAdminDTO dto = new TrocaAdminDTO();
            dto.setId(t.getId());
            dto.setTipo(t.getTipo());
            dto.setStatus(t.getStatus());
            dto.setDataSolicitacao(t.getDataSolicitacao());
            dto.setIdPedido(t.getVenda().getId());

            List<TrocaAdminDTO.ItemDTO> itens = t.getItens().stream().map(item -> {
                TrocaAdminDTO.ItemDTO i = new TrocaAdminDTO.ItemDTO();
                i.setNomeProduto(item.getProduto().getNome());
                i.setQuantidade(item.getQuantidade());
                return i;
            }).toList();

            dto.setItens(itens);
            return dto;
        }).toList();
    }
}