package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.venda.modelo.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepositorio extends JpaRepository<Venda, Long> {
}