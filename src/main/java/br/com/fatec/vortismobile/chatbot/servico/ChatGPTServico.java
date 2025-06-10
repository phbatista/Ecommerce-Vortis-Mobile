package br.com.fatec.vortismobile.chatbot.servico;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.estoque.repositorio.EstoqueRepositorio;
import br.com.fatec.vortismobile.produto.modelo.Categoria;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.CategoriaRepositorio;
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
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    public String gerarResposta(Long idCliente, List<Map<String, String>> mensagens) throws IOException {
        List<Produto> produtos = produtoRepositorio.findAll();
        Map<String, List<Produto>> produtosPorCategoria = new HashMap<>();
        StringBuilder contextoProdutos = new StringBuilder(); // ✅ agora no lugar certo

        int contador = 1;

        for (Produto p : produtos) {
            if (!"ATIVO".equalsIgnoreCase(p.getStatus().name())) continue;

            int qtdEstoque = estoqueRepositorio.obterQuantidadeDisponivel(p.getId());
            String precoOuIndisponivel = "Indisponível";
            if (qtdEstoque > 0) {
                List<Estoque> estoqueList = estoqueRepositorio.buscarEstoqueMaisRecente(p.getId());
                if (!estoqueList.isEmpty()) {
                    precoOuIndisponivel = "R$ " + estoqueList.get(0).getPrecoVenda();
                }
            }

            for (Categoria c : p.getCategorias()) {
                produtosPorCategoria
                        .computeIfAbsent(c.getNome(), k -> new ArrayList<>())
                        .add(p);
            }

            contextoProdutos.append(String.format("""
            %d. **%s**
                    - 💾 %s RAM | %s armazenamento
                    - 🔋 %s mAh | %s
                    - %s

            """, contador++,
                    p.getNome(),
                    p.getMemoriaRam(),
                    p.getArmazenamento(),
                    p.getBateria(),
                    p.getSistemaOperacional(),
                    precoOuIndisponivel
            ));
        }

        List<Produto> comprados = produtoRepositorio.buscarCompradosPorCliente(idCliente);
        StringBuilder historico = new StringBuilder("O cliente já comprou:\n");
        for (Produto p : comprados) {
            historico.append("- ").append(p.getNome()).append(" (")
                    .append(p.getMemoriaRam()).append(" RAM, ")
                    .append(p.getArmazenamento()).append(", ")
                    .append(p.getSistemaOperacional()).append(")\n");
        }

        List<Categoria> categorias = categoriaRepositorio.findAll();
        StringBuilder categoriasTexto = new StringBuilder("Categorias disponíveis:\n");
        for (Categoria c : categorias) {
            categoriasTexto.append("- ").append(c.getNome()).append("\n");
        }

        String contexto = historico + "\n\nVocê é o VortisBot, assistente virtual da Vortis Mobile.\n" +
                "Sua função é recomendar smartphones com base nas preferências do cliente.\n" +
                "- Se o cliente já disser para quem é o celular (ex: filho, mãe, idoso), **não pergunte de novo**.\n" +
                "- Faça perguntas relevantes só se ainda não foram respondidas.\n" +
                "- Considere as categorias como diferenciais.\n\n" +
                categoriasTexto + "\n\nProdutos disponíveis:\n" + contextoProdutos;

        List<Map<String, String>> mensagensComSistema = new ArrayList<>();
        mensagensComSistema.add(Map.of("role", "system", "content", contexto));
        mensagensComSistema.addAll(mensagens);

        Map<String, Object> payload = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", mensagensComSistema
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