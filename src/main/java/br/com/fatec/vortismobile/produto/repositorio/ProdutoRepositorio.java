package br.com.fatec.vortismobile.produto.repositorio;

import br.com.fatec.vortismobile.produto.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepositorio extends JpaRepository<Produto, Long> {
    @Query("""
    SELECT p FROM Produto p
    JOIN ItemVenda iv ON iv.produto = p
    JOIN Venda v ON v.id = iv.venda.id
    WHERE v.cliente.id = :idCliente
""")
    List<Produto> buscarCompradosPorCliente(@Param("idCliente") Long idCliente);
}
