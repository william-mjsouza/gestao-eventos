package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;
import com.gestaoeventos.dominio.evento.evento.EventoException;
import com.gestaoeventos.dominio.evento.lote.LoteException;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaException;
import com.gestaoeventos.dominio.participante.pessoa.ParticipanteException;
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

    @ExceptionHandler(AvaliacaoException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoException(AvaliacaoException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(ListaEsperaException.class)
    public ResponseEntity<Map<String, String>> handleListaEsperaException(ListaEsperaException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(GestaoEventoException.class)
    public ResponseEntity<Map<String, String>> handleGestaoEventoException(GestaoEventoException ex) {
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
