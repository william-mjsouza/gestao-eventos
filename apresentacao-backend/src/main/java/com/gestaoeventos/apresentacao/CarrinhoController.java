package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.inscricao.carrinho.Carrinho;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    @Autowired
    private CarrinhoServico carrinhoServico;

    @PostMapping
    public ResponseEntity<Carrinho> adicionarAoCarrinho(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());
        Long loteId = Long.valueOf(payload.get("loteId").toString());

        Carrinho carrinho = carrinhoServico.adicionarAoCarrinho(cpf, eventoId, loteId);

        return ResponseEntity.status(HttpStatus.CREATED).body(carrinho);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<Inscricao> finalizarCompra(@PathVariable Long id) {
        // Retorna a Inscrição confirmada após pagar o carrinho
        Inscricao inscricaoConfirmada = carrinhoServico.finalizarCompra(id);
        return ResponseEntity.ok(inscricaoConfirmada);
    }
}