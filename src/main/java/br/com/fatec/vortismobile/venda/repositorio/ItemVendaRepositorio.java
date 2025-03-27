package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemVendaRepositorio extends JpaRepository<ItemVenda, Long> {
}
