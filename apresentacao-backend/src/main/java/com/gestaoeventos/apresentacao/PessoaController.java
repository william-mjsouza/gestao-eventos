package com.gestaoeventos.apresentacao;

import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaServico;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participantes")
public class PessoaController {

    @Autowired
    private PessoaServico pessoaServico;

    @PostMapping
    public ResponseEntity<Pessoa> cadastrar(@Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaSalvo = pessoaServico.salvar(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalvo);
    }
}
