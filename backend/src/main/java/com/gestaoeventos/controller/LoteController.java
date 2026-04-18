package com.gestaoeventos.controller;

import com.gestaoeventos.entity.Lote;
import com.gestaoeventos.service.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos/{eventoId}/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    @GetMapping("/ativo")
    public ResponseEntity<Lote> obterLoteAtivo(@PathVariable Long eventoId) {
        Lote loteAtivo = loteService.obterLoteAtivo(eventoId);
        return ResponseEntity.ok(loteAtivo);
    }
}