package br.com.fatec.vortismobile.cliente.repositorio;

import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
}
