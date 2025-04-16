package br.com.fatec.vortismobile.venda.controlador;

import br.com.fatec.vortismobile.venda.dto.PedidoAdminDTO;
import br.com.fatec.vortismobile.venda.dto.PedidoRespostaDTO;
import br.com.fatec.vortismobile.venda.dto.VendaDTO;
import br.com.fatec.vortismobile.venda.servico.VendaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaControlador {

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


}