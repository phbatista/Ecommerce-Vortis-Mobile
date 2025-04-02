package br.com.fatec.vortismobile.venda.servico;

import br.com.fatec.vortismobile.carrinho.servico.FreteServico;
import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import br.com.fatec.vortismobile.cliente.modelo.Endereco;
import br.com.fatec.vortismobile.cliente.repositorio.ClienteRepositorio;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import br.com.fatec.vortismobile.venda.dto.VendaDTO;
import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.repositorio.ItemVendaRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.VendaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VendaServico {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private FreteServico freteServico;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private VendaRepositorio vendaRepositorio;

    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio;

    public Long criarVenda(VendaDTO dto) {
        Cliente cliente = clienteRepositorio.findById(dto.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Optional<Endereco> enderecoEntregaOpt = cliente.getEnderecos().stream()
                .filter(e -> "Entrega".equalsIgnoreCase(e.getTipoEndereco()))
                .filter(e -> e.getId().equals(dto.getIdEnderecoEntrega()))
                .findFirst();

        if (enderecoEntregaOpt.isEmpty()) {
            throw new RuntimeException("Endereço de entrega inválido para este cliente.");
        }

        Endereco enderecoEntrega = enderecoEntregaOpt.get();
        String cep = enderecoEntrega.getCep();

        double frete = freteServico.calcularFrete(dto.getItens(), cep);

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setFrete(frete);
        venda = vendaRepositorio.save(venda); // salva e recupera com ID

        for (VendaDTO.ItemDTO itemDTO : dto.getItens()) {
            Produto produto = produtoRepositorio.findById(itemDTO.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(produto.getPrecoVenda());
            item.setVenda(venda);

            itemVendaRepositorio.save(item);
        }

        return venda.getId();
    }
}