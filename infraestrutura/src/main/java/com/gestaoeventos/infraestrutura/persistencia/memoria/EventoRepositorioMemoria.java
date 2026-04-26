package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EventoRepositorioMemoria implements EventoRepositorio {

    private final Map<Long, Evento> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Optional<Evento> findById(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Evento save(Evento evento) {
        if (evento.getId() == null) {
            evento.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(evento.getId(), evento);
        return evento;
    }

    @Override
    public boolean existsByNome(String nome) {
        return armazenamento.values().stream()
                .anyMatch(e -> e.getNome().equals(nome));
    }

    @Override
    public boolean existeColisaoLocalEHorario(String local, LocalDateTime inicio, LocalDateTime termino) {
        return armazenamento.values().stream()
                .filter(e -> e.getLocal().equals(local))
                .filter(e -> e.getStatus() != StatusEvento.CANCELADO)
                .anyMatch(e -> e.getDataHoraInicio().isBefore(termino)
                        && e.getDataHoraTermino().isAfter(inicio));
    }
}
