package com.gestaoeventos.dominio.participante.pessoa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepositorio extends JpaRepository<Pessoa, String> {
    boolean existsByEmail(String email);
    Optional<Pessoa> findByEmail(String email);
    void salvar(Pessoa pessoa);
    Optional<Pessoa> buscarPorCpf(String cpf);
    void atualizarSaldo(Pessoa pessoa);
}
