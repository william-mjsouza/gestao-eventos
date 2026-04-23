package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.inscricao.inscricao.CancelamentoInscricaoServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoServico inscricaoServico;

    @Autowired
    private CancelamentoInscricaoServico cancelamentoServico;

    @PostMapping
    public ResponseEntity<Inscricao> iniciarInscricao(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());
        Long loteId = Long.valueOf(payload.get("loteId").toString());

        Inscricao inscricao = inscricaoServico.iniciarInscricao(cpf, eventoId, loteId);

        return ResponseEntity.status(HttpStatus.CREATED).body(inscricao);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Inscricao> confirmarPagamento(@PathVariable Long id) {
        Inscricao inscricaoConfirmada = inscricaoServico.confirmarPagamento(id);
        return ResponseEntity.ok(inscricaoConfirmada);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Inscricao> cancelarInscricao(@PathVariable Long id) {
        Inscricao inscricaoCancelada = cancelamentoServico.executar(id);
        return ResponseEntity.ok(inscricaoCancelada);
    }
}
