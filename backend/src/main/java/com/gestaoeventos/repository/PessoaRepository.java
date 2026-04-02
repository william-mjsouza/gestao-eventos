package com.gestaoeventos.repository;

import com.gestaoeventos.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa,String> {
    boolean existsByEmail(String email);
    Optional<Pessoa> findByEmail(String email);

}
