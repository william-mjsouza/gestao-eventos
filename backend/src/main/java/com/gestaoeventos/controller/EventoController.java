package com.gestaoeventos.controller;

import com.gestaoeventos.entity.Evento;
import com.gestaoeventos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping
    public ResponseEntity<Evento> criar(@Valid @RequestBody Evento evento) {
        Evento eventoSalvo = eventoService.salvar(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }
}