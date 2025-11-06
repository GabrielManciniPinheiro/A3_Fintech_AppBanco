package br.com.fintech.fintech_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
public class UsuarioCreateDTO {
    @NotEmpty(message = "Nome é obrigatório")
    private String nome;
    @NotEmpty(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;
}