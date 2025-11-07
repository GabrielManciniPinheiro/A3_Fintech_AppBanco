package br.com.fintech.fintech_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioInfoDTO {
    private String nome;
    private String cpf;
}