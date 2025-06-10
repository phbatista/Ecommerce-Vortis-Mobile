package br.com.fatec.vortismobile.venda.repositorio;

import br.com.fatec.vortismobile.venda.modelo.CupomTroca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CupomTrocaRepositorio extends JpaRepository<CupomTroca, Long> {
    Optional<CupomTroca> findByCodigoIgnoreCase(String codigo);
}