package br.com.fatec.vortismobile.venda.controlador;

import br.com.fatec.vortismobile.venda.dto.SolicitacaoTrocaDevolucaoDTO;
import br.com.fatec.vortismobile.venda.servico.TrocaDevolucaoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}