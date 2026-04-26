package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InscricaoRepositorio extends JpaRepository<Inscricao, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoId(Long eventoId);
    long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status);

    @Query("SELECT i FROM Inscricao i " +
            "WHERE i.participante.cpf = :cpf " +
            "AND i.status = com.gestaoeventos.dominio.compartilhado.StatusInscricao.CONFIRMADA " +
            "AND i.evento.dataHoraInicio < :fim " +
            "AND i.evento.dataHoraFim > :inicio")
    List<Inscricao> buscarConflitos(
            @Param("cpf") String cpf,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
}