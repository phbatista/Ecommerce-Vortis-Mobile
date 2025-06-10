package br.com.fatec.vortismobile.notificacao.repositorio;

import br.com.fatec.vortismobile.notificacao.modelo.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepositorio extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByClienteId(Long idCliente);
}