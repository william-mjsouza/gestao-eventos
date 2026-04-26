package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ListaEsperaRepositorioMemoria implements ListaEsperaRepositorio {

    private final Map<Long, ListaEspera> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public ListaEspera save(ListaEspera listaEspera) {
        if (listaEspera.getId() == null) {
            listaEspera.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(listaEspera.getId(), listaEspera);
        return listaEspera;
    }

    @Override
    public Optional<ListaEspera> findById(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return armazenamento.values().stream()
                .anyMatch(l -> l.getParticipante().getCpf().equals(cpf)
                        && l.getEvento().getId().equals(eventoId));
    }

    @Override
    public long countByEventoIdAndStatus(Long eventoId, StatusListaEspera status) {
        return armazenamento.values().stream()
                .filter(l -> l.getEvento().getId().equals(eventoId))
                .filter(l -> l.getStatus() == status)
                .count();
    }

    @Override
    public Optional<ListaEspera> findByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return armazenamento.values().stream()
                .filter(l -> l.getParticipante().getCpf().equals(cpf)
                        && l.getEvento().getId().equals(eventoId))
                .findFirst();
    }

    @Override
    public List<ListaEspera> findByEventoIdAndStatusOrderByPosicaoAsc(Long eventoId, StatusListaEspera status) {
        return armazenamento.values().stream()
                .filter(l -> l.getEvento().getId().equals(eventoId))
                .filter(l -> l.getStatus() == status)
                .sorted(Comparator.comparingInt(ListaEspera::getPosicao))
                .collect(Collectors.toList());
    }
}
