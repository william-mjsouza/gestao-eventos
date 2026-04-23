package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.inscricao.listaespera.ListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/lista-espera")
public class ListaEsperaController {

    @Autowired
    private ListaEsperaServico listaEsperaServico;

    @PostMapping
    public ResponseEntity<ListaEspera> entrarNaFila(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());

        ListaEspera entrada = listaEsperaServico.entrarNaFila(cpf, eventoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrada);
    }

    @PostMapping("/pagar")
    public ResponseEntity<ListaEspera> confirmarPagamento(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());

        ListaEspera confirmado = listaEsperaServico.confirmarPagamento(cpf, eventoId);
        return ResponseEntity.ok(confirmado);
    }
}
