package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.inscricao.avaliacao.Avaliacao;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoServico avaliacaoServico;

    @PostMapping
    public ResponseEntity<Avaliacao> avaliar(@RequestBody Map<String, Object> payload) {

        int nota = (int) payload.get("nota");
        String comentario = (String) payload.get("comentario");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());
        String cpf = (String) payload.get("cpf");

        Avaliacao avaliacaoSalva = avaliacaoServico.salvar(nota, comentario, eventoId, cpf);

        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoSalva);
    }
}
