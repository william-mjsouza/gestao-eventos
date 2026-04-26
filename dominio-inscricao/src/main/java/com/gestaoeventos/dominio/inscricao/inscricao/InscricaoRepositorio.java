package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InscricaoRepositorio extends JpaRepository<Inscricao, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    long countByEventoId(Long eventoId);
    long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status);

    boolean existsByParticipanteCpfAndEventoIdAndStatus(String cpf, Long eventoId, StatusInscricao status);
}