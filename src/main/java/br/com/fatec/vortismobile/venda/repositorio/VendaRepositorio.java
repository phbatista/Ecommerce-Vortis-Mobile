package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.cliente.modelo.Cliente;
import br.com.fatec.vortismobile.venda.modelo.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepositorio extends JpaRepository<Venda, Long> {
    List<Venda> findByClienteId(Long idCliente);

    List<Venda> findByDataVendaBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}