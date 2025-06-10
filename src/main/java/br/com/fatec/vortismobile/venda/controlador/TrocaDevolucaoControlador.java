package br.com.fatec.vortismobile.venda.controlador;

import br.com.fatec.vortismobile.venda.dto.SolicitacaoTrocaDevolucaoDTO;
import br.com.fatec.vortismobile.venda.dto.TrocaAdminDTO;
import br.com.fatec.vortismobile.venda.dto.TrocaClienteDTO;
import br.com.fatec.vortismobile.venda.servico.TrocaDevolucaoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class TrocaDevolucaoControlador {

    @Autowired
    private TrocaDevolucaoServico trocaDevolucaoServico;

    @PostMapping("/solicitar-troca-ou-devolucao")
    public ResponseEntity<?> solicitarTrocaOuDevolucao(@RequestBody SolicitacaoTrocaDevolucaoDTO dto) {
        try {
            trocaDevolucaoServico.solicitarTrocaOuDevolucao(dto);
            return ResponseEntity.ok("Solicitação registrada com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/trocas/{id}/confirmar")
    public ResponseEntity<?> confirmarRecebimento(@PathVariable Long id,
                                                  @RequestParam boolean retornarEstoque) {
        try {
            trocaDevolucaoServico.confirmarRecebimento(id, retornarEstoque);
            return ResponseEntity.ok("Recebimento confirmado com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<TrocaClienteDTO>> listarTrocasDoCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(trocaDevolucaoServico.listarPorCliente(idCliente));
    }

    @GetMapping("/trocas")
    public ResponseEntity<List<TrocaAdminDTO>> listarTodasAsTrocas() {
        return ResponseEntity.ok(trocaDevolucaoServico.listarTodasParaAdmin());
    }
}