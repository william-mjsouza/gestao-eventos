package com.gestaoeventos.controller;

import com.gestaoeventos.entity.Participante;
import com.gestaoeventos.service.ParticipanteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;

    @PostMapping
    public ResponseEntity<Participante> cadastrar(@Valid @RequestBody Participante participante) {

        Participante participanteSalvo = participanteService.salvar(participante);

        return ResponseEntity.status(HttpStatus.CREATED).body(participanteSalvo);
    }
}