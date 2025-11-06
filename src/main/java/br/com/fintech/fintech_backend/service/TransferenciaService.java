package br.com.fintech.fintech_backend.service;

import br.com.fintech.fintech_backend.dto.TransferenciaRequestDTO;
import br.com.fintech.fintech_backend.dto.TransferenciaResponseDTO;

public interface TransferenciaService {
    TransferenciaResponseDTO realizarTransferencia(TransferenciaRequestDTO requestDTO);
}