package br.com.fintech.fintech_backend.controller;

import br.com.fintech.fintech_backend.dto.UsuarioCreateDTO;
import br.com.fintech.fintech_backend.model.Usuario;
import br.com.fintech.fintech_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> criarUsuario(@Valid @RequestBody UsuarioCreateDTO dto) {
        Usuario usuario = usuarioService.criarUsuarioEConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário " + usuario.getNome() + " e conta " + usuario.getConta().getNumeroConta() + " criados.");
    }

    // --- MÉTODO (ENDPOINT) INTEIRO ADICIONADO ---
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String cpf) {
        usuarioService.deletarUsuario(cpf);
        // A resposta padrão para um DELETE bem-sucedido é 204 No Content
        return ResponseEntity.noContent().build();
    }
}