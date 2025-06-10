package br.com.fatec.vortismobile.cupom.repositorio;

import br.com.fatec.vortismobile.cupom.modelo.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CupomRepositorio extends JpaRepository<Cupom, Long> {
    Optional<Cupom> findByCodigoAndAtivoTrue(String codigo);
}
