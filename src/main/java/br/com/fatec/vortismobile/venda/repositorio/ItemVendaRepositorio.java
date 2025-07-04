package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.venda.modelo.ItemVenda;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemVendaRepositorio extends JpaRepository<ItemVenda, Long> {
    List<ItemVenda> findByVenda(Venda venda);
}
