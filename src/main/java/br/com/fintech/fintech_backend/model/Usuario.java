package br.com.fintech.fintech_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Conta conta;
    @OneToMany(mappedBy = "remetente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasEnviadas;
    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasRecebidas;

    public Usuario(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }
}