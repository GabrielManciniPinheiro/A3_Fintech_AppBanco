package br.com.fintech.fintech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {

    private String mensagem;
    private String nomeUsuario;
    private String token; // Nosso "passe" de segurança
}