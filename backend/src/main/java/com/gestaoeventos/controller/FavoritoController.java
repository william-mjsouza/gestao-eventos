package com.gestaoeventos.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gestaoeventos.entity.Favorito;
import com.gestaoeventos.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired private FavoritoService favoritoService;

    @PostMapping("/toggle")
    public ResponseEntity<String> alternarFavorito(@RequestBody Map<String, Object> payload) {
        String cpf = (String) payload.get("cpf");
        Long eventoId = Long.valueOf(payload.get("eventoId").toString());

        String resultado = favoritoService.toggleFavorito(cpf, eventoId);
        System.out.println("Resultado do Service: " + resultado);

        return ResponseEntity.ok("Resposta do servidor: " + resultado);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<List<Favorito>> listar(@PathVariable String cpf) {
        List<Favorito> lista = favoritoService.listarFavoritos(cpf);


        System.out.println("====================================");
        System.out.println("DEBUG - CPF recebido na URL: '" + cpf + "'");
        System.out.println("DEBUG - Quantos favoritos o banco achou: " + lista.size());
        System.out.println("====================================");

        return ResponseEntity.ok(lista);
    }
}