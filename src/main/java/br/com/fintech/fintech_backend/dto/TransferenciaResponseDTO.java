package br.com.fintech.fintech_backend.dto;

import br.com.fintech.fintech_backend.model.StatusTransferencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransferenciaResponseDTO {
    private Long idTransferencia;
    private StatusTransferencia status;
    private String mensagem;
    private LocalDateTime dataHora;
}