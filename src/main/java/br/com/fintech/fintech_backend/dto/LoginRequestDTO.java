package br.com.fintech.fintech_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank
    private String cpf;

    @NotBlank
    private String senha;
}