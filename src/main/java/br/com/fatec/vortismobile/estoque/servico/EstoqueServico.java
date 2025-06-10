package br.com.fatec.vortismobile.estoque.servico;

import br.com.fatec.vortismobile.estoque.dto.EstoqueDTO;
import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.estoque.repositorio.EstoqueRepositorio;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import br.com.fatec.vortismobile.produto.repositorio.ProdutoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EstoqueServico {

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    public Estoque salvar(EstoqueDTO dto) {
        Produto produto = produtoRepositorio.findById(dto.getIdProduto())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // 1. Validar dados obrigatórios (RN0051, RN0061, RN0062, RNF0064)
        if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }
        if (dto.getCustoUnitario() == null || dto.getCustoUnitario() <= 0) {
            throw new IllegalArgumentException("Custo unitário inválido.");
        }
        if (dto.getDataEntrada() == null) {
            throw new IllegalArgumentException("Data de entrada obrigatória.");
        }

        // 2. Verificar se já existem entradas com custo diferente para o mesmo produto (RN005x)
        List<Estoque> entradasAnteriores = estoqueRepositorio.findByProduto(produto);
        BigDecimal custoMaior = BigDecimal.valueOf(dto.getCustoUnitario());

        for (Estoque entrada : entradasAnteriores) {
            if (entrada.getCustoUnitario().compareTo(custoMaior) > 0) {
                custoMaior = entrada.getCustoUnitario();
            }
        }

        // 3. Calcular o preço de venda com base no maior custo e grupo de precificação (RF0052)
        BigDecimal margem = switch (produto.getGrupoPrecificacao().toLowerCase()) {
            case "premium" -> BigDecimal.valueOf(0.80);
            case "intermediario" -> BigDecimal.valueOf(0.60);
            case "padrao" -> BigDecimal.valueOf(0.40);
            default -> throw new RuntimeException("Grupo de precificação inválido.");
        };

        BigDecimal precoVenda = custoMaior.add(custoMaior.multiply(margem)).setScale(2, BigDecimal.ROUND_HALF_UP);

        // 4. Registrar a nova entrada com custo informado, mas preçoVenda calculado
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setDataEntrada(dto.getDataEntrada());
        estoque.setFornecedor(dto.getFornecedor());
        estoque.setCustoUnitario(BigDecimal.valueOf(dto.getCustoUnitario()));
        estoque.setQuantidade(dto.getQuantidade());
        estoque.setPrecoVenda(precoVenda); // novo campo

        return estoqueRepositorio.save(estoque);
    }

    public void reporPorTroca(Produto produto, int quantidade) {
        Estoque entrada = new Estoque();
        entrada.setProduto(produto);
        entrada.setQuantidade(quantidade);
        entrada.setDataEntrada(java.time.LocalDate.now());
        entrada.setFornecedor("TROCA");
        entrada.setCustoUnitario(java.math.BigDecimal.ZERO);

        estoqueRepositorio.save(entrada);
    }
}