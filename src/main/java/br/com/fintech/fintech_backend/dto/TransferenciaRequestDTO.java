package br.com.fintech.fintech_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;
import java.math.BigDecimal;

@Data
public class TransferenciaRequestDTO {
    @NotEmpty(message = "CPF do remetente é obrigatório")
    @CPF(message = "CPF do remetente inválido")
    private String cpfRemetente;
    @NotEmpty(message = "CPF do destinatário é obrigatório")
    @CPF(message = "CPF do destinatário inválido")
    private String cpfDestinatario;
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
    private BigDecimal valor;
}