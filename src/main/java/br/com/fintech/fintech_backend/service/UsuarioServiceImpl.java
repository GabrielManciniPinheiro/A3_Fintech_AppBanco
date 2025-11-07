package br.com.fintech.fintech_backend.service;

import br.com.fintech.fintech_backend.dto.UsuarioCreateDTO;
import br.com.fintech.fintech_backend.exception.ResourceNotFoundException;
import br.com.fintech.fintech_backend.model.Conta;
import br.com.fintech.fintech_backend.model.Usuario;
import br.com.fintech.fintech_backend.repository.ContaRepository;
import br.com.fintech.fintech_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User; // --- IMPORT ADICIONADO ---
import org.springframework.security.core.userdetails.UserDetails; // --- IMPORT ADICIONADO ---
import org.springframework.security.core.userdetails.UserDetailsService; // --- IMPORT ADICIONADO ---
import org.springframework.security.core.userdetails.UsernameNotFoundException; // --- IMPORT ADICIONADO ---
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // --- IMPORT ADICIONADO ---
import java.util.Random;

@Service
@RequiredArgsConstructor
// --- INTERFACE ADICIONADA ---
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Usuario criarUsuarioEConta(UsuarioCreateDTO dto) {

        if (!dto.getSenha().equals(dto.getConfirmarSenha())) {
            throw new ResourceNotFoundException("As senhas não coincidem.");
        }
        if (usuarioRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new ResourceNotFoundException("CPF já cadastrado.");
        }
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("Email já cadastrado.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.getSenha());

        Usuario novoUsuario = new Usuario(
                dto.getNome(),
                dto.getCpf(),
                dto.getEmail(),
                dto.getTelefone(),
                senhaCriptografada
        );

        String numeroConta = String.format("%04d-%d", new Random().nextInt(10000), new Random().nextInt(9));
        Conta novaConta = new Conta(numeroConta, novoUsuario);
        novoUsuario.setConta(novaConta);

        usuarioRepository.save(novoUsuario);
        return novoUsuario;
    }

    @Override
    @Transactional
    public void deletarUsuario(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com CPF: " + cpf));

        usuarioRepository.delete(usuario);
    }

    // --- MÉTODO NOVO ADICIONADO (EXIGIDO PELO UserDetailsService) ---
    // É este método que o Spring Security vai usar para carregar o usuário
    // durante o login e na validação do token.
    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        // O "username" para nós é o CPF
        return usuarioRepository.findByCpf(cpf)
                .map(user -> new User(
                        user.getCpf(),
                        user.getSenha(),
                        new ArrayList<>())) // Autoridades/Roles (vazio por enquanto)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com CPF: " + cpf));
    }
}