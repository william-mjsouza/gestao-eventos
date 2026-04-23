package com.gestaoeventos.dominio.participante.pessoa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PessoaServico {
    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    public Pessoa salvar(Pessoa pessoa) {
        if (pessoaRepositorio.existsById(pessoa.getCpf())) {
            throw new ParticipanteException("CPF já cadastrado no sistema.");
        }
        if (pessoaRepositorio.existsByEmail(pessoa.getEmail())) {
            throw new ParticipanteException("Email já cadastrado no sistema.");
        }
        if (pessoa.getSaldo() != null && pessoa.getSaldo() < 0) {
            throw new ParticipanteException("Saldo nao pode ser negativo.");
        }

        return pessoaRepositorio.save(pessoa);
    }
}
