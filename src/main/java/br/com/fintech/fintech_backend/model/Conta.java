package br.com.fintech.fintech_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas")
@Data
@NoArgsConstructor
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String numeroConta;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal saldo;
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    public Conta(String numeroConta, Usuario usuario) {
        this.numeroConta = numeroConta;
        this.usuario = usuario;
        this.saldo = BigDecimal.ZERO;
    }
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
    }
}