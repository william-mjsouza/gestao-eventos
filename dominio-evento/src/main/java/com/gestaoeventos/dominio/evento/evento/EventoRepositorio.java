package com.gestaoeventos.dominio.evento.evento;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventoRepositorio {
    Optional<Evento> findById(Long id);
    Evento save(Evento evento);
    boolean existsByNome(String nome);
    boolean existeColisaoLocalEHorario(String local, LocalDateTime inicio, LocalDateTime termino);
}
