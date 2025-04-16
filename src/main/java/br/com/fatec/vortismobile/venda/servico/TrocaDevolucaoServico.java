package br.com.fatec.vortismobile.venda.servico;

import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import br.com.fatec.vortismobile.venda.dto.SolicitacaoTrocaDevolucaoDTO;
import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.repositorio.ItemVendaRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.VendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrocaDevolucaoServico {

    @Autowired
    private VendaRepositorio vendaRepositorio;

    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    public void solicitarTrocaOuDevolucao(SolicitacaoTrocaDevolucaoDTO dto) {
        Venda venda = vendaRepositorio.findById(dto.getIdPedido())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        Produto produto = produtoRepositorio.findById(dto.getIdProduto())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        ItemVenda item = itemVendaRepositorio.findByVenda(venda).stream()
                .filter(i -> i.getProduto().getId().equals(produto.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Produto não encontrado no pedido."));

        if (dto.getQuantidade() <= 0 || dto.getQuantidade() > item.getQuantidade()) {
            throw new RuntimeException("Quantidade inválida para troca/devolução.");
        }

        // aqui você pode alterar o status da venda e registrar a solicitação
        if (dto.getTipo().equalsIgnoreCase("TROCA")) {
            venda.setStatus("TROCA SOLICITADA");
        } else if (dto.getTipo().equalsIgnoreCase("DEVOLUCAO")) {
            venda.setStatus("DEVOLUÇÃO SOLICITADA");
        } else {
            throw new RuntimeException("Tipo de solicitação inválido.");
        }

        vendaRepositorio.save(venda);
    }
}