package br.com.fintech.fintech_backend.repository;

import br.com.fintech.fintech_backend.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByUsuarioCpf(String cpf);
}