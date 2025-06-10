package br.com.fatec.vortismobile.estoque.repositorio;

import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepositorio extends JpaRepository<Estoque, Long> {

    @Query("""
    SELECT e FROM Estoque e
    WHERE e.produto.id = :idProduto
    ORDER BY e.dataEntrada DESC""")
    List<Estoque> buscarEstoqueMaisRecente(@Param("idProduto") Long idProduto);

    @Query("""
    SELECT COALESCE(SUM(e.quantidade), 0) FROM Estoque e
    WHERE e.produto.id = :idProduto""")

    int obterQuantidadeDisponivel(@Param("idProduto") Long idProduto);

    List<Estoque> findByProduto(Produto produto);
    Optional<Estoque> findTopByProdutoOrderByDataEntradaDesc(Produto produto);
    List<Estoque> findByProdutoOrderByDataEntradaAsc(Produto produto);
}