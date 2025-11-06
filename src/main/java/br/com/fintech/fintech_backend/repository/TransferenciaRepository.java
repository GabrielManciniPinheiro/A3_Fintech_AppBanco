package br.com.fintech.fintech_backend.repository;

import br.com.fintech.fintech_backend.model.StatusTransferencia;
import br.com.fintech.fintech_backend.model.Transferencia;
import br.com.fintech.fintech_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    // Regra 1
    List<Transferencia> findTop10ByRemetenteAndStatusOrderByDataHoraDesc(Usuario remetente, StatusTransferencia status);
    // Regra 3
    long countByRemetenteAndDataHoraBetween(Usuario remetente, LocalDateTime inicio, LocalDateTime fim);
    // Regra 5
    List<Transferencia> findByRemetenteAndDataHoraBetween(Usuario remetente, LocalDateTime inicio, LocalDateTime fim);
    // Regra 6
    long countByRemetenteAndStatusAndDataHoraBetween(Usuario remetente, StatusTransferencia status, LocalDateTime inicio, LocalDateTime fim);
    // Regra 7
    long countByRemetenteAndIndiceFraudeGreaterThan(Usuario remetente, Double valor);
}
