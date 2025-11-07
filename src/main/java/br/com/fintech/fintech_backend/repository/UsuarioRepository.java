package br.com.fintech.fintech_backend.repository;

import br.com.fintech.fintech_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCpf(String cpf);

    // Método adicionado para validar email duplicado
    Optional<Usuario> findByEmail(String email);
}