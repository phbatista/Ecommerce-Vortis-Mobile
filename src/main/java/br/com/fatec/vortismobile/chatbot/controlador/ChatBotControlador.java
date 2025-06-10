package br.com.fatec.vortismobile.chatbot.controlador;

import br.com.fatec.vortismobile.chatbot.servico.ChatGPTServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotControlador {

    @Autowired
    private ChatGPTServico chatGPTService;

    @PostMapping
    public ResponseEntity<Map<String, String>> conversar(@RequestBody Map<String, Object> body) {
        try {
            Long idCliente = Long.parseLong(body.get("idCliente").toString());
            List<Map<String, String>> mensagens = (List<Map<String, String>>) body.get("mensagens");

            String resposta = chatGPTService.gerarResposta(idCliente, mensagens);
            return ResponseEntity.ok(Map.of("resposta", resposta));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("resposta", "Erro ao processar sua solicitação."));
        }
    }
}