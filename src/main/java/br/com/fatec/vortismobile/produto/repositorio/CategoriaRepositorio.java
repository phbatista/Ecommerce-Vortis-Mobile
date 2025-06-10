package br.com.fatec.vortismobile.produto.repositorio;

import br.com.fatec.vortismobile.produto.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepositorio extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);
}