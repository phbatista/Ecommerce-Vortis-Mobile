package br.com.fatec.vortismobile.produto.repositorio;

import br.com.fatec.vortismobile.produto.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {
}