package com.gestaoeventos.controller;

import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @PostMapping
    public ResponseEntity<Pessoa> cadastrar(@Valid @RequestBody Pessoa pessoa) {

        Pessoa pessoaSalvo = pessoaService.salvar(pessoa);

        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalvo);
    }
}