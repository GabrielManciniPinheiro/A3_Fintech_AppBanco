package br.com.fintech.fintech_backend.exception;

import br.com.fintech.fintech_backend.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FraudeDetectadaException.class)
    public ResponseEntity<ErrorResponseDTO> handleFraudeException(FraudeDetectadaException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), ex.getMessage(), null, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponseDTO> handleSaldoException(SaldoInsuficienteException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "Erro de validação", erros, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro interno inesperado.", null, LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}