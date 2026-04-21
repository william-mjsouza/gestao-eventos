package com.gestaoeventos.controller;

import com.gestaoeventos.entity.Avaliacao;
import com.gestaoeventos.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<Avaliacao> avaliar(@RequestBody Map<String, Object> payload) {

        int nota = (int) payload.get("nota");
        String comentario = (String) payload.get("comentario");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());
        String cpf = (String) payload.get("cpf");

        Avaliacao avaliacaoSalva = avaliacaoService.salvar(nota, comentario, eventoId, cpf);

        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoSalva);
    }
}