package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

import java.util.List;

@Repository
public interface InscricaoRepositorio extends JpaRepository<Inscricao, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    boolean existsByParticipanteCpfAndEventoIdAndStatus(String cpf, Long eventoId, StatusInscricao status);
    long countByEventoId(Long eventoId);
    long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status);

    @Query("SELECT i FROM Inscricao i " +
            "WHERE i.participante.cpf = :cpf " +
            "AND i.status = com.gestaoeventos.dominio.compartilhado.StatusInscricao.CONFIRMADA " +
            "AND i.evento.dataHoraInicio < :fim " +
            "AND i.evento.dataHoraTermino > :inicio")
    List<Inscricao> buscarConflitos(
            @Param("cpf") String cpf,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    long countByParticipanteCpfAndEventoIdAndStatusIn(String cpf, Long eventoId, java.util.List<StatusInscricao> statuses);
    List<Inscricao> findByEventoIdAndStatusIn(Long eventoId, List<StatusInscricao> statuses);
    boolean existsByParticipanteCpfAndEventoIdAndCupomCodigo(String cpf, Long eventoId, String cupomCodigo);
}
