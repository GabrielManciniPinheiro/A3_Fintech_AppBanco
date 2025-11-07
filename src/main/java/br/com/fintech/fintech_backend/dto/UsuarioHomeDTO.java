package br.com.fintech.fintech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class UsuarioHomeDTO {
    private String nome;
    private String cpf;
    private String numeroConta;
    private BigDecimal saldo;
}