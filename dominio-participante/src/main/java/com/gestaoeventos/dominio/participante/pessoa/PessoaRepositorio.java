package com.gestaoeventos.dominio.participante.pessoa;

import java.util.Optional;

public interface PessoaRepositorio {
    Optional<Pessoa> findById(String cpf);
    Pessoa save(Pessoa pessoa);
    boolean existsById(String cpf);
    boolean existsByEmail(String email);
    Optional<Pessoa> findByEmail(String email);
}
