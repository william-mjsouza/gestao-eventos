package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class EventoRepositorioImpl implements EventoRepositorio {

    private final EventoRepositorioJpa jpa;

    public EventoRepositorioImpl(EventoRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Evento> findById(Long id) { return jpa.findById(id); }

    @Override
    public Evento save(Evento evento) { return jpa.save(evento); }

    @Override
    public boolean existsByNome(String nome) { return jpa.existsByNome(nome); }

    @Override
    public boolean existeColisaoLocalEHorario(String local, LocalDateTime inicio, LocalDateTime termino) {
        return jpa.existeColisaoLocalEHorario(local, inicio, termino);
    }
}
