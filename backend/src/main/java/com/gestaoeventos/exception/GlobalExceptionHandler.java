package com.gestaoeventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParticipanteException.class)
    public ResponseEntity<Map<String, String>> handleParticipanteException(ParticipanteException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(EventoException.class)
    public ResponseEntity<Map<String, String>> handleEventoException(EventoException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(InscricaoException.class)
    public ResponseEntity<Map<String, String>> handleInscricaoException(InscricaoException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }
    
    @ExceptionHandler(LoteException.class)
    public ResponseEntity<Map<String, String>> handleLoteException(LoteException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }
}