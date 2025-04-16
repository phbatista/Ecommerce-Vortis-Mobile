package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.cliente.modelo.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepositorio extends JpaRepository<Cartao, Long> {
}
