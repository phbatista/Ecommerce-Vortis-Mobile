package br.com.fatec.vortismobile.repositorio;

import br.com.fatec.vortismobile.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
}
