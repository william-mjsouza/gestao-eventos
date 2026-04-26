package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InscricaoRepositorio {
    Optional<Inscricao> findById(Long id);
    Inscricao save(Inscricao inscricao);
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoId(Long eventoId);
    long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status);
    List<Inscricao> buscarConflitos(String cpf, LocalDateTime inicio, LocalDateTime fim);
    long countByParticipanteCpfAndEventoIdAndStatusIn(String cpf, Long eventoId, List<StatusInscricao> statuses);
    List<Inscricao> findByEventoIdAndStatusIn(Long eventoId, List<StatusInscricao> statuses);
    boolean existsByParticipanteCpfAndEventoIdAndCupomCodigo(String cpf, Long eventoId, String cupomCodigo);
    boolean existsByParticipanteCpfAndEventoIdAndStatus(String cpf, Long eventoId, StatusInscricao status);
}
