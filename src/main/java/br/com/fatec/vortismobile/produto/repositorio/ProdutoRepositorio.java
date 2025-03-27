package br.com.fatec.vortismobile.produto.repositorio;

import br.com.fatec.vortismobile.produto.modelo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepositorio extends JpaRepository<Produto, Long> {
}
