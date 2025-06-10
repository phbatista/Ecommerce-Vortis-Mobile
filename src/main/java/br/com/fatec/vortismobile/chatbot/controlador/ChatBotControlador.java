package br.com.fatec.vortismobile.chatbot.controlador;

import br.com.fatec.vortismobile.chatbot.servico.ChatGPTServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotControlador {

    @Autowired
    private ChatGPTServico chatGPTService;

    @PostMapping
    public ResponseEntity<Map<String, String>> conversar(@RequestBody Map<String, String> body) {
        String pergunta = body.get("mensagem");
        try {
            String resposta = chatGPTService.gerarResposta(pergunta);
            return ResponseEntity.ok(Map.of("resposta", resposta));
        } catch (Exception e) {
            e.printStackTrace(); // <- isso mostra o erro exato no console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao processar sua solicitação."));
        }
    }
}
