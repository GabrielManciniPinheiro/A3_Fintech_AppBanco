package br.com.fintech.fintech_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioCreateDTO {
    @NotBlank(message = "Nome não pode ser vazio")
    private String nome;

    @NotBlank(message = "CPF não pode ser vazio")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Email inválido")
    private String email;

    private String telefone; // Opcional

    @NotBlank(message = "Senha não pode ser vazia")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Confirmação de senha não pode ser vazia")
    private String confirmarSenha;
}