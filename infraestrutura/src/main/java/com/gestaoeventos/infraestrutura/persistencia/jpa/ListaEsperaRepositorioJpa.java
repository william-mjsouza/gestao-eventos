package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListaEsperaRepositorioJpa extends JpaRepository<ListaEspera, Long> {
    List<ListaEspera> findByEventoIdAndStatusOrderByPosicaoAsc(Long eventoId, StatusListaEspera status);
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoIdAndStatus(Long eventoId, StatusListaEspera status);
    Optional<ListaEspera> findByParticipanteCpfAndEventoId(String cpf, Long eventoId);
}
