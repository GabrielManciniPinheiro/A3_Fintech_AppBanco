package br.com.fintech.fintech_backend.controller;

import br.com.fintech.fintech_backend.dto.TransferenciaRequestDTO;
import br.com.fintech.fintech_backend.dto.TransferenciaResponseDTO;
import br.com.fintech.fintech_backend.service.TransferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    @PostMapping("/pix")
    public ResponseEntity<TransferenciaResponseDTO> realizarTransferenciaPix(
            @Valid @RequestBody TransferenciaRequestDTO requestDTO) {
        TransferenciaResponseDTO response = transferenciaService.realizarTransferencia(requestDTO);
        return ResponseEntity.ok(response);
    }
}