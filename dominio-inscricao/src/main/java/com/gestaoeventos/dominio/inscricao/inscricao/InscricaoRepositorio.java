package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscricaoRepositorio extends JpaRepository<Inscricao, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoId(Long eventoId);
    long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status);
    List<Inscricao> findByEventoIdAndStatusIn(Long eventoId, List<StatusInscricao> statuses);
}
