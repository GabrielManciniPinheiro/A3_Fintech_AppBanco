package br.com.fintech.fintech_backend.service;

import br.com.fintech.fintech_backend.dto.TransferenciaRequestDTO;
import br.com.fintech.fintech_backend.dto.TransferenciaResponseDTO;
import br.com.fintech.fintech_backend.exception.FraudeDetectadaException;
import br.com.fintech.fintech_backend.exception.ResourceNotFoundException;
import br.com.fintech.fintech_backend.exception.SaldoInsuficienteException;
import br.com.fintech.fintech_backend.model.*;
import br.com.fintech.fintech_backend.repository.ContaRepository;
import br.com.fintech.fintech_backend.repository.TransferenciaRepository;
import br.com.fintech.fintech_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferenciaServiceImpl implements TransferenciaService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final TransferenciaRepository transferenciaRepository;

    private static final double LIMITE_FRAUDE = 0.31;

    @Override
    @Transactional
    public TransferenciaResponseDTO realizarTransferencia(TransferenciaRequestDTO requestDTO) {
        if (requestDTO.getCpfRemetente().equals(requestDTO.getCpfDestinatario())) {
            throw new ResourceNotFoundException("Remetente e Destinatário não podem ser os mesmos.");
        }
        Conta contaRemetente = contaRepository.findByUsuarioCpf(requestDTO.getCpfRemetente())
                .orElseThrow(() -> new ResourceNotFoundException("Conta do remetente não encontrada."));
        Conta contaDestinatario = contaRepository.findByUsuarioCpf(requestDTO.getCpfDestinatario())
                .orElseThrow(() -> new ResourceNotFoundException("Conta do destinatário não encontrada."));

        if (contaRemetente.getSaldo().compareTo(requestDTO.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }

        Transferencia transferencia = new Transferencia(
                contaRemetente.getUsuario(),
                contaDestinatario.getUsuario(),
                requestDTO.getValor()
        );
        transferencia.setStatus(StatusTransferencia.PENDENTE);
        transferencia = transferenciaRepository.save(transferencia);

        double indiceFraude = calcularIndiceFraude(transferencia, contaRemetente, contaDestinatario);
        transferencia.setIndiceFraude(indiceFraude);

        if (indiceFraude > LIMITE_FRAUDE) {
            log.warn("Tentativa de fraude bloqueada! CPF: {}, Índice: {}", requestDTO.getCpfRemetente(), indiceFraude);
            transferencia.setStatus(StatusTransferencia.FALHA_FRAUDE);
            transferenciaRepository.save(transferencia);
            throw new FraudeDetectadaException(
                    String.format("Transação bloqueada por suspeita de fraude (Índice: %.2f).", indiceFraude)
            );
        }

        contaRemetente.setSaldo(contaRemetente.getSaldo().subtract(requestDTO.getValor()));
        contaDestinatario.setSaldo(contaDestinatario.getSaldo().add(requestDTO.getValor()));
        contaRepository.save(contaRemetente);
        contaRepository.save(contaDestinatario);

        transferencia.setStatus(StatusTransferencia.CONCLUIDA);
        Transferencia txConcluida = transferenciaRepository.save(transferencia);

        log.info("Transferência Concluída: ID {}", txConcluida.getId());
        return new TransferenciaResponseDTO(
                txConcluida.getId(),
                txConcluida.getStatus(),
                "Transferência realizada com sucesso.",
                txConcluida.getDataHora()
        );
    }

    private double calcularIndiceFraude(Transferencia tx, Conta contaRemetente, Conta contaDestinatario) {
        double indice = 0.0;
        Usuario remetente = tx.getRemetente();
        LocalDateTime agora = tx.getDataHora();

        if (checkRegra1_ValorAcimaMedia(remetente, tx.getValor())) indice += 0.15;
        if (checkRegra2_ContaDestinatarioRecente(contaDestinatario, agora)) indice += 0.1;
        if (checkRegra3_AltoVolume24h(remetente, agora)) indice += 0.15;
        if (checkRegra4_HorarioIncomum(agora)) indice += 0.2;
        if (checkRegra5_MultiplosDestinatarios10min(remetente, agora)) indice += 0.3;
        if (checkRegra6_CancelamentosRecentes7d(remetente, agora)) indice += 0.2;
        if (checkRegra7_HistoricoSuspeito(remetente)) indice += 0.25;

        log.info("Cálculo de fraude: CPF: {}, Índice Total: {}", remetente.getCpf(), indice);
        return indice;
    }

    // --- Métodos Auxiliares para as Regras de Fraude ---
    private boolean checkRegra1_ValorAcimaMedia(Usuario remetente, BigDecimal valorAtual) {
        List<Transferencia> ultimas10 = transferenciaRepository.findTop10ByRemetenteAndStatusOrderByDataHoraDesc(
                remetente, StatusTransferencia.CONCLUIDA);
        if (ultimas10.isEmpty()) return false;
        BigDecimal soma = ultimas10.stream().map(Transferencia::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal media = soma.divide(BigDecimal.valueOf(ultimas10.size()), 4, RoundingMode.HALF_UP);
        BigDecimal mediaMais150pct = media.multiply(BigDecimal.valueOf(1.50));
        return valorAtual.compareTo(mediaMais150pct) > 0;
    }

    private boolean checkRegra2_ContaDestinatarioRecente(Conta contaDestinatario, LocalDateTime dataTx) {
        return ChronoUnit.DAYS.between(contaDestinatario.getDataCriacao(), dataTx) < 5;
    }

    private boolean checkRegra3_AltoVolume24h(Usuario remetente, LocalDateTime dataTx) {
        long count = transferenciaRepository.countByRemetenteAndDataHoraBetween(remetente, dataTx.minusHours(24), dataTx);
        return count > 5;
    }

    private boolean checkRegra4_HorarioIncomum(LocalDateTime dataTx) {
        LocalTime horaTx = dataTx.toLocalTime();
        return horaTx.isAfter(LocalTime.MIDNIGHT) && horaTx.isBefore(LocalTime.of(5, 0));
    }

    private boolean checkRegra5_MultiplosDestinatarios10min(Usuario remetente, LocalDateTime dataTx) {
        List<Transferencia> txs10min = transferenciaRepository.findByRemetenteAndDataHoraBetween(remetente, dataTx.minusMinutes(10), dataTx);
        long destinatariosUnicos = txs10min.stream()
                .map(tx -> tx.getDestinatario().getId())
                .distinct()
                .count();
        return destinatariosUnicos >= 5;
    }

    private boolean checkRegra6_CancelamentosRecentes7d(Usuario remetente, LocalDateTime dataTx) {
        long cancelamentos = transferenciaRepository.countByRemetenteAndStatusAndDataHoraBetween(
                remetente, StatusTransferencia.FALHA_FRAUDE, dataTx.minusDays(1), dataTx);
        return cancelamentos > 0;
    }

    private boolean checkRegra7_HistoricoSuspeito(Usuario remetente) {
        long txSuspeitas = transferenciaRepository.countByRemetenteAndIndiceFraudeGreaterThan(remetente, 0.30);
        return txSuspeitas > 0;
    }
}