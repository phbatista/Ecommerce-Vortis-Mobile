package br.com.fatec.vortismobile.notificacao.controlador;

import br.com.fatec.vortismobile.notificacao.servico.NotificacaoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoControlador {

    @Autowired
    private NotificacaoServico notificacaoServico;

    @GetMapping("/{idCliente}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(notificacaoServico.listarPorCliente(idCliente));
    }
}