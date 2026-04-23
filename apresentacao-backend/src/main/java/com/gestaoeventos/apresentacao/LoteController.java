package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eventos/{eventoId}/lotes")
public class LoteController {

    @Autowired
    private LoteServico loteServico;

    @GetMapping("/ativo")
    public ResponseEntity<Lote> obterLoteAtivo(@PathVariable Long eventoId) {
        Lote loteAtivo = loteServico.obterLoteAtivo(eventoId);
        return ResponseEntity.ok(loteAtivo);
    }
}
