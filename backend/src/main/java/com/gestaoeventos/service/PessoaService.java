package com.gestaoeventos.service;

import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.exception.ParticipanteException;
import com.gestaoeventos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PessoaService {
    @Autowired
    private PessoaRepository pessoaRepository;
    public Pessoa salvar(Pessoa pessoa){
        if(pessoaRepository.existsById(pessoa.getCpf())){
            throw new ParticipanteException("CPF já cadastrado no sistema.");
        }
        if(pessoaRepository.existsByEmail(pessoa.getEmail())){
            throw new ParticipanteException("Email já cadastrado no sistema.");
        }
        if(pessoa.getSaldo() != null && pessoa.getSaldo() < 0){
            throw new ParticipanteException("Saldo nao pode ser negativo.");
        }

        return pessoaRepository.save(pessoa);

    }
}
