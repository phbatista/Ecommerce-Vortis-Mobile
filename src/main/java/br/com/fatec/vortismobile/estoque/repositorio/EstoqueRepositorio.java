package br.com.fatec.vortismobile.estoque.repositorio;

import br.com.fatec.vortismobile.estoque.modelo.Estoque;
import br.com.fatec.vortismobile.produto.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstoqueRepositorio extends JpaRepository<Estoque, Long> {
    List<Estoque> findByProdutoId(Long produtoId);
    List<Estoque> findByProduto(Produto produto);
    List<Estoque> findByProdutoOrderByDataEntradaAsc(Produto produto);
}