package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ListaEsperaRepositorioImpl implements ListaEsperaRepositorio {

    private final ListaEsperaRepositorioJpa jpa;

    public ListaEsperaRepositorioImpl(ListaEsperaRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public ListaEspera save(ListaEspera listaEspera) { return jpa.save(listaEspera); }

    @Override
    public Optional<ListaEspera> findById(Long id) { return jpa.findById(id); }

    @Override
    public boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return jpa.existsByParticipanteCpfAndEventoId(cpf, eventoId);
    }

    @Override
    public long countByEventoIdAndStatus(Long eventoId, StatusListaEspera status) {
        return jpa.countByEventoIdAndStatus(eventoId, status);
    }

    @Override
    public Optional<ListaEspera> findByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return jpa.findByParticipanteCpfAndEventoId(cpf, eventoId);
    }

    @Override
    public List<ListaEspera> findByEventoIdAndStatusOrderByPosicaoAsc(Long eventoId, StatusListaEspera status) {
        return jpa.findByEventoIdAndStatusOrderByPosicaoAsc(eventoId, status);
    }
}
