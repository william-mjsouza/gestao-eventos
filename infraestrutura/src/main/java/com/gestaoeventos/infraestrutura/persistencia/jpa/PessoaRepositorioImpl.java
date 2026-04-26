package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PessoaRepositorioImpl implements PessoaRepositorio {

    private final PessoaRepositorioJpa jpa;

    public PessoaRepositorioImpl(PessoaRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Pessoa> findById(String cpf) { return jpa.findById(cpf); }

    @Override
    public Pessoa save(Pessoa pessoa) { return jpa.save(pessoa); }

    @Override
    public boolean existsById(String cpf) { return jpa.existsById(cpf); }

    @Override
    public boolean existsByEmail(String email) { return jpa.existsByEmail(email); }

    @Override
    public Optional<Pessoa> findByEmail(String email) { return jpa.findByEmail(email); }
}
