package com.gestaoeventos.repository;

import com.gestaoeventos.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    boolean existsByNome(String nome);
}