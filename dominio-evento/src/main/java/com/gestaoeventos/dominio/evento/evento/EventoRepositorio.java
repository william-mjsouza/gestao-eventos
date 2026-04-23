package com.gestaoeventos.dominio.evento.evento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepositorio extends JpaRepository<Evento, Long> {
    boolean existsByNome(String nome);
}
