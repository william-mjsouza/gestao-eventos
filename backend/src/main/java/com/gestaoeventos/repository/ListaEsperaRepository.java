package com.gestaoeventos.repository;

import com.gestaoeventos.entity.ListaEspera;
import com.gestaoeventos.entity.StatusListaEspera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {


    List<ListaEspera> findByEventoIdAndStatusOrderByPosicaoAsc(Long eventoId, StatusListaEspera status);


    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoIdAndStatus(Long eventoId, StatusListaEspera status);
    Optional<ListaEspera> findByParticipanteCpfAndEventoId(String cpf, Long eventoId);
}