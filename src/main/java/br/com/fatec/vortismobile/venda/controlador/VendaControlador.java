package br.com.fatec.vortismobile.venda.controlador;

import br.com.fatec.vortismobile.cliente.modelo.Endereco;
import br.com.fatec.vortismobile.venda.dto.PedidoRespostaDTO;
import br.com.fatec.vortismobile.venda.dto.VendaDTO;
import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.modelo.VendaCartao;
import br.com.fatec.vortismobile.venda.repositorio.ItemVendaRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.VendaCartaoRepositorio;
import br.com.fatec.vortismobile.venda.repositorio.VendaRepositorio;
import br.com.fatec.vortismobile.venda.servico.VendaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaControlador {

    @Autowired
    private VendaRepositorio vendaRepositorio;

    @Autowired
    private ItemVendaRepositorio itemVendaRepositorio;

    @Autowired
    private VendaCartaoRepositorio vendaCartaoRepositorio;

    @Autowired
    private VendaServico vendaServico;

    @PostMapping
    public ResponseEntity<?> criarVenda(@RequestBody VendaDTO dto) {
        try {
            Long idVenda = vendaServico.criarVenda(dto);
            return ResponseEntity.ok(idVenda); // retorna ID da venda para o front exibir
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/admin")
    public ResponseEntity<List<PedidoRespostaDTO>> listarTodos() {
        return ResponseEntity.ok(vendaServico.listarTodosPedidos());
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<Void> atualizarStatusPedido(@PathVariable Long id, @RequestParam String status) {
        vendaServico.atualizarStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meus-pedidos/{idCliente}")
    public ResponseEntity<List<PedidoRespostaDTO>> meusPedidos(@PathVariable Long idCliente) {
        return ResponseEntity.ok(vendaServico.listarPedidosDoCliente(idCliente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoRespostaDTO> buscarPorId(@PathVariable Long id) {
        Venda venda = vendaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        PedidoRespostaDTO dto = new PedidoRespostaDTO();
        dto.setId(venda.getId());
        dto.setDataVenda(venda.getDataVenda());
        dto.setStatus(venda.getStatus());

        // Cliente
        dto.setClienteNome(venda.getCliente().getNome());

        // Produtos
        List<PedidoRespostaDTO.ItemDTO> itensDTO = new ArrayList<>();
        List<ItemVenda> itens = itemVendaRepositorio.findByVenda(venda);
        double total = 0;
        for (ItemVenda item : itens) {
            PedidoRespostaDTO.ItemDTO i = new PedidoRespostaDTO.ItemDTO();
            i.setProdutoNome(item.getProduto().getNome());
            i.setIdProduto(item.getProduto().getId());
            i.setQuantidade(item.getQuantidade());
            i.setPreco(item.getPrecoUnitario());
            i.setSubtotal(item.getQuantidade() * item.getPrecoUnitario());
            itensDTO.add(i);
            total += i.getSubtotal();
        }
        dto.setProdutos(itensDTO);

        // Endereço
        Endereco e = venda.getEnderecoEntrega();
        dto.setEndereco(String.format("%s %s, %s - %s, %s",
                e.getTipoLogradouro(), e.getLogradouro(), e.getNumero(),
                e.getBairro(), e.getCidade()));

        // Cartões
        List<VendaCartao> cartoes = vendaCartaoRepositorio.findByVenda(venda);
        List<PedidoRespostaDTO.CartaoDTO> cartoesDTO = new ArrayList<>();
        for (VendaCartao v : cartoes) {
            PedidoRespostaDTO.CartaoDTO c = new PedidoRespostaDTO.CartaoDTO();
            c.setFinalNumero("**** " + v.getCartao().getCartaoNumero().substring(v.getCartao().getCartaoNumero().length() - 4));
            c.setValor(v.getValor());
            cartoesDTO.add(c);
        }
        dto.setCartoes(cartoesDTO);

        dto.setCupomPromocional(venda.getCupomPromocional());
        dto.setCupomTroca(venda.getCupomTroca());
        dto.setFrete(venda.getFrete());
        dto.setTotal(total + venda.getFrete());

        return ResponseEntity.ok(dto);
    }

}