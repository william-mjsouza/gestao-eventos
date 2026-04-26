package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PessoaRepositorioJpa extends JpaRepository<Pessoa, String> {
    boolean existsByEmail(String email);
    Optional<Pessoa> findByEmail(String email);
}
