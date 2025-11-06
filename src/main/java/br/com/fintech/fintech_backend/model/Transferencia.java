package br.com.fintech.fintech_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transferencias")
@Data
@NoArgsConstructor
public class Transferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Usuario remetente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valor;
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora;
    @Column(nullable = false)
    private Double indiceFraude;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransferencia status;

    public Transferencia(Usuario remetente, Usuario destinatario, BigDecimal valor) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.valor = valor;
    }
    @PrePersist
    protected void onPersist() {
        this.dataHora = LocalDateTime.now();
        this.indiceFraude = 0.0;
        if (this.status == null) {
            this.status = StatusTransferencia.PENDENTE;
        }
    }
}