package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PessoaRepositorioMemoria implements PessoaRepositorio {

    private final Map<String, Pessoa> armazenamento = new ConcurrentHashMap<>();

    @Override
    public Optional<Pessoa> findById(String cpf) {
        return Optional.ofNullable(armazenamento.get(cpf));
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        armazenamento.put(pessoa.getCpf(), pessoa);
        return pessoa;
    }

    @Override
    public boolean existsById(String cpf) {
        return armazenamento.containsKey(cpf);
    }

    @Override
    public boolean existsByEmail(String email) {
        return armazenamento.values().stream()
                .anyMatch(p -> p.getEmail().equals(email));
    }

    @Override
    public Optional<Pessoa> findByEmail(String email) {
        return armazenamento.values().stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst();
    }
}
