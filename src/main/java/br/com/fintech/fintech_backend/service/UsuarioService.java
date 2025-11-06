package br.com.fintech.fintech_backend.service;

import br.com.fintech.fintech_backend.dto.UsuarioCreateDTO;
import br.com.fintech.fintech_backend.model.Usuario;

public interface UsuarioService {
    Usuario criarUsuarioEConta(UsuarioCreateDTO dto);

    // --- LINHA ADICIONADA ---
    void deletarUsuario(String cpf);
}