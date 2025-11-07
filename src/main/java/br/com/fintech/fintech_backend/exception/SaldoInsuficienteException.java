package br.com.fintech.fintech_backend.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) { super(message); }
}