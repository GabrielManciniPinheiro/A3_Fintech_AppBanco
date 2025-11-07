package br.com.fintech.fintech_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String telefone;

    @Column(nullable = false)
    private String senha;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Conta conta;

    @OneToMany(mappedBy = "remetente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasEnviadas;

    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasRecebidas;

    // Construtor atualizado
    public Usuario(String nome, String cpf, String email, String telefone, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
    }
}