package com.gestaoeventos.dominio.inscricao.listaespera;

import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;

import java.util.List;
import java.util.Optional;

public interface ListaEsperaRepositorio {
    ListaEspera save(ListaEspera listaEspera);
    Optional<ListaEspera> findById(Long id);
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoIdAndStatus(Long eventoId, StatusListaEspera status);
    Optional<ListaEspera> findByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    List<ListaEspera> findByEventoIdAndStatusOrderByPosicaoAsc(Long eventoId, StatusListaEspera status);
}
