package br.com.fintech.fintech_backend.controller;

import br.com.fintech.fintech_backend.dto.LoginRequestDTO;
import br.com.fintech.fintech_backend.dto.LoginResponseDTO;
import br.com.fintech.fintech_backend.exception.ResourceNotFoundException;
import br.com.fintech.fintech_backend.model.Usuario;
import br.com.fintech.fintech_backend.repository.UsuarioRepository;
import br.com.fintech.fintech_backend.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {

        Usuario usuario = usuarioRepository.findByCpf(loginRequest.getCpf())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ou senha inválidos."));

        if (passwordEncoder.matches(loginRequest.getSenha(), usuario.getSenha())) {

            String token = tokenService.generateToken(usuario);
            return ResponseEntity.ok(new LoginResponseDTO(
                    "Login realizado com sucesso!",
                    usuario.getNome(),
                    token // Retornamos o token para o frontend
            ));
        } else {
            throw new ResourceNotFoundException("Usuário ou senha inválidos.");
        }
    }
}