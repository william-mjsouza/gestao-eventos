package com.gestaoeventos.controller;

import com.gestaoeventos.entity.ListaEspera;
import com.gestaoeventos.service.ListaEsperaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lista-espera")
public class ListaEsperaController {

    @Autowired
    private ListaEsperaService listaEsperaService;

    @PostMapping
    public ResponseEntity<ListaEspera> entrarNaFila(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());

        ListaEspera entrada = listaEsperaService.entrarNaFila(cpf, eventoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrada);
    }

    @PostMapping("/pagar")
    public ResponseEntity<ListaEspera> confirmarPagamento(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());

        ListaEspera confirmado = listaEsperaService.confirmarPagamento(cpf, eventoId);
        return ResponseEntity.ok(confirmado);
    }
}