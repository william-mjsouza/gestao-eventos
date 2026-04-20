package com.gestaoeventos.controller;

import com.gestaoeventos.entity.Inscricao;
import com.gestaoeventos.service.InscricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gestaoeventos.service.CancelamentoInscricaoService;
import java.util.Map;

@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoService inscricaoService;

    @Autowired
    private CancelamentoInscricaoService cancelamentoService;

    @PostMapping
    public ResponseEntity<Inscricao> iniciarInscricao(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());
        Long loteId = Long.valueOf(payload.get("loteId").toString());

        Inscricao inscricao = inscricaoService.iniciarInscricao(cpf, eventoId, loteId);

        return ResponseEntity.status(HttpStatus.CREATED).body(inscricao);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Inscricao> confirmarPagamento(@PathVariable Long id) {
        Inscricao inscricaoConfirmada = inscricaoService.confirmarPagamento(id);
        return ResponseEntity.ok(inscricaoConfirmada);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Inscricao> cancelarInscricao(@PathVariable Long id) {
        Inscricao inscricaoCancelada = cancelamentoService.executar(id);
        return ResponseEntity.ok(inscricaoCancelada);
    }
}