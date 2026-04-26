package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class InscricaoRepositorioImpl implements InscricaoRepositorio {

    private final InscricaoRepositorioJpa jpa;

    public InscricaoRepositorioImpl(InscricaoRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Inscricao> findById(Long id) { return jpa.findById(id); }

    @Override
    public Inscricao save(Inscricao inscricao) { return jpa.save(inscricao); }

    @Override
    public boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return jpa.existsByParticipanteCpfAndEventoId(cpf, eventoId);
    }

    @Override
    public long countByEventoId(Long eventoId) { return jpa.countByEventoId(eventoId); }

    @Override
    public long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status) {
        return jpa.countByEventoIdAndStatusNot(eventoId, status);
    }

    @Override
    public List<Inscricao> buscarConflitos(String cpf, LocalDateTime inicio, LocalDateTime fim) {
        return jpa.buscarConflitos(cpf, inicio, fim);
    }

    @Override
    public long countByParticipanteCpfAndEventoIdAndStatusIn(String cpf, Long eventoId, List<StatusInscricao> statuses) {
        return jpa.countByParticipanteCpfAndEventoIdAndStatusIn(cpf, eventoId, statuses);
    }

    @Override
    public List<Inscricao> findByEventoIdAndStatusIn(Long eventoId, List<StatusInscricao> statuses) {
        return jpa.findByEventoIdAndStatusIn(eventoId, statuses);
    }

    @Override
    public boolean existsByParticipanteCpfAndEventoIdAndCupomCodigo(String cpf, Long eventoId, String cupomCodigo) {
        return jpa.existsByParticipanteCpfAndEventoIdAndCupomCodigo(cpf, eventoId, cupomCodigo);
    }
    @Override
    public boolean existsByParticipanteCpfAndEventoIdAndStatus(String cpf, Long eventoId, StatusInscricao status) {
        return jpa.existsByParticipanteCpfAndEventoIdAndStatus(cpf, eventoId, status);
    }
}
