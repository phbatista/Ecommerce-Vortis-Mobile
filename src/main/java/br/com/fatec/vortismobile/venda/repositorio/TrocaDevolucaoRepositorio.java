package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.venda.modelo.TrocaDevolucao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrocaDevolucaoRepositorio extends JpaRepository<TrocaDevolucao, Long> {
    List<TrocaDevolucao> findByVendaClienteId(Long idCliente);
}
