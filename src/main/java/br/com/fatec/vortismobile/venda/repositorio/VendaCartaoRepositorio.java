package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.venda.modelo.Venda;
import br.com.fatec.vortismobile.venda.modelo.VendaCartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendaCartaoRepositorio extends JpaRepository<VendaCartao, Long> {
    List<VendaCartao> findByVenda(Venda venda);
}