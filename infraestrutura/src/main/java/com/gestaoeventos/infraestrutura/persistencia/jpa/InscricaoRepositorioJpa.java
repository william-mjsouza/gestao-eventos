package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InscricaoRepositorioJpa extends JpaRepository<Inscricao, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoId(Long eventoId);
    long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status);

    @Query("SELECT i FROM Inscricao i " +
           "WHERE i.participante.cpf = :cpf " +
           "AND i.status = com.gestaoeventos.dominio.compartilhado.StatusInscricao.CONFIRMADA " +
           "AND i.evento.dataHoraInicio < :fim " +
           "AND i.evento.dataHoraTermino > :inicio")
    List<Inscricao> buscarConflitos(@Param("cpf") String cpf,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

    long countByParticipanteCpfAndEventoIdAndStatusIn(String cpf, Long eventoId, List<StatusInscricao> statuses);
    List<Inscricao> findByEventoIdAndStatusIn(Long eventoId, List<StatusInscricao> statuses);
    boolean existsByParticipanteCpfAndEventoIdAndCupomCodigo(String cpf, Long eventoId, String cupomCodigo);
}
