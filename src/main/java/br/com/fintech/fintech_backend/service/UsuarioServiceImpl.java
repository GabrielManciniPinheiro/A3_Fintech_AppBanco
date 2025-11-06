package br.com.fintech.fintech_backend.service;

import br.com.fintech.fintech_backend.dto.UsuarioCreateDTO;
import br.com.fintech.fintech_backend.exception.ResourceNotFoundException;
import br.com.fintech.fintech_backend.model.Conta;
import br.com.fintech.fintech_backend.model.Usuario;
import br.com.fintech.fintech_backend.repository.ContaRepository;
import br.com.fintech.fintech_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;

    @Override
    @Transactional
    public Usuario criarUsuarioEConta(UsuarioCreateDTO dto) {
        if (usuarioRepository.findByCpf(dto.getCpf()).isPresent()) {
            throw new ResourceNotFoundException("CPF já cadastrado");
        }
        Usuario novoUsuario = new Usuario(dto.getNome(), dto.getCpf());
        String numeroConta = String.format("%04d-%d", new Random().nextInt(10000), new Random().nextInt(9));
        Conta novaConta = new Conta(numeroConta, novoUsuario);
        novoUsuario.setConta(novaConta);
        usuarioRepository.save(novoUsuario);
        return novoUsuario;
    }

    // --- MÉTODO INTEIRO ADICIONADO ---
    @Override
    @Transactional
    public void deletarUsuario(String cpf) {
        // 1. Encontrar o usuário pelo CPF
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com CPF: " + cpf));

        // 2. Deletar o usuário
        // Graças ao CascadeType.ALL que definimos no @OneToOne e @OneToMany,
        // o JPA irá deletar a conta e as transferências associadas
        // automaticamente (em cascata).
        usuarioRepository.delete(usuario);
    }
}