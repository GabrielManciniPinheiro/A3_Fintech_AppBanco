package br.com.fintech.fintech_backend.controller;

import br.com.fintech.fintech_backend.dto.UsuarioCreateDTO;
import br.com.fintech.fintech_backend.dto.UsuarioHomeDTO;
import br.com.fintech.fintech_backend.dto.UsuarioInfoDTO;
import br.com.fintech.fintech_backend.exception.ResourceNotFoundException;
import br.com.fintech.fintech_backend.model.Usuario;
import br.com.fintech.fintech_backend.repository.UsuarioRepository;
import br.com.fintech.fintech_backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    // (Endpoint PÚBLICO - definido no SecurityConfig)
    @PostMapping
    public ResponseEntity<String> criarUsuario(@Valid @RequestBody UsuarioCreateDTO dto) {
        Usuario usuario = usuarioService.criarUsuarioEConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuário " + usuario.getNome() + " e conta " + usuario.getConta().getNumeroConta() + " criados.");
    }

    // (Endpoint PROTEGIDO - exige token)
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String cpf) {
        usuarioService.deletarUsuario(cpf);
        return ResponseEntity.noContent().build();
    }

    // ENDPOINT NOVO (PROTEGIDO) - Para a Tela Home
    @GetMapping("/me")
    public ResponseEntity<UsuarioHomeDTO> getUsuarioLogado() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String cpf = authentication.getName(); // Pega o CPF do token

        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        UsuarioHomeDTO dto = new UsuarioHomeDTO(
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getConta().getNumeroConta(),
                usuario.getConta().getSaldo()
        );
        return ResponseEntity.ok(dto);
    }

    // ENDPOINT NOVO (PROTEGIDO) - Para buscar destinatário
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UsuarioInfoDTO> getUsuarioInfoPorCpf(@PathVariable String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário destinatário não encontrado."));

        UsuarioInfoDTO dto = new UsuarioInfoDTO(
                usuario.getNome(),
                usuario.getCpf()
        );
        return ResponseEntity.ok(dto);
    }
}