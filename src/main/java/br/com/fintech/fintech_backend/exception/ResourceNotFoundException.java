package br.com.fintech.fintech_backend.exception;

// Arquivo 1: ResourceNotFoundException.java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}