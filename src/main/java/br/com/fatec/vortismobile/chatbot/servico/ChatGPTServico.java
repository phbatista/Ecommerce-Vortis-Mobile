package br.com.fatec.vortismobile.chatbot.servico;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import br.com.fatec.vortismobile.produto.modelo.Categoria;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class ChatGPTServico {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    public String gerarResposta(String mensagem) throws IOException {

        List<Produto> produtos = produtoRepositorio.findAll();

        Map<String, List<Produto>> produtosPorCategoria = new HashMap<>();

        for (Produto p : produtos) {
            for (Categoria c : p.getCategorias()) {
                produtosPorCategoria
                        .computeIfAbsent(c.getNome(), k -> new ArrayList<>())
                        .add(p);
            }
        }

        StringBuilder contextoProdutos = new StringBuilder();

        for (Map.Entry<String, List<Produto>> entry : produtosPorCategoria.entrySet()) {
            contextoProdutos.append("\nCategoria: ").append(entry.getKey()).append("\n");
            for (Produto p : entry.getValue()) {
                contextoProdutos.append(String.format("- %s | R$ %.2f | %s, %s, %s, %s\n",
                        p.getNome(),
                        p.getMemoriaRam(),
                        p.getArmazenamento(),
                        p.getSistemaOperacional(),
                        p.getBateria()
                ));
            }
        }

        String contexto = """
Você é um assistente da Vortis Mobile. Recomende smartphones com base nas preferências do cliente (ex: jogos, câmera, custo-benefício).
Use os produtos abaixo como base. Sugira apenas os que estão listados por categoria:
""" + contextoProdutos.toString();

        Map<String, Object> payload = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", contexto),
                        Map.of("role", "user", "content", mensagem)
                )
        );

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        HttpPost post = new HttpPost(API_URL);
        post.setHeader("Authorization", "Bearer " + API_KEY);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String respostaJson = EntityUtils.toString(response.getEntity());
            System.out.println("Resposta bruta da API: " + respostaJson); // 👈 debug

            Map<String, Object> map = mapper.readValue(respostaJson, Map.class);

            if (map.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) map.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

            return "Desculpe, não consegui gerar uma resposta no momento.";

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}