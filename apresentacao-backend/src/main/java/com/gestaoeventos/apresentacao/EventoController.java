package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoServico;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private EventoServico eventoServico;

    @PostMapping
    public ResponseEntity<Evento> criar(@Valid @RequestBody Evento evento) {
        Evento eventoSalvo = eventoServico.salvar(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSalvo);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Evento> alterarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        StatusEvento novoStatus = StatusEvento.valueOf(payload.get("status").toUpperCase());
        Evento eventoAtualizado = eventoServico.alterarStatus(id, novoStatus);
        return ResponseEntity.ok(eventoAtualizado);
    }
}
