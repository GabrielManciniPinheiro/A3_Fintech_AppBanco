package br.com.fintech.fintech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String mensagem;
    private List<String> erros;
    private LocalDateTime timestamp;
}