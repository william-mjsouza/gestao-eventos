package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InscricaoRepositorioMemoria implements InscricaoRepositorio {

    private final Map<Long, Inscricao> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Optional<Inscricao> findById(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Inscricao save(Inscricao inscricao) {
        if (inscricao.getId() == null) {
            inscricao.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(inscricao.getId(), inscricao);
        return inscricao;
    }

    @Override
    public boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return armazenamento.values().stream()
                .anyMatch(i -> i.getParticipante().getCpf().equals(cpf)
                        && i.getEvento().getId().equals(eventoId));
    }

    @Override
    public boolean existsByParticipanteCpfAndEventoIdAndStatus(String cpf, Long eventoId, StatusInscricao status) {
        return armazenamento.values().stream()
                .anyMatch(i -> i.getParticipante().getCpf().equals(cpf)
                        && i.getEvento().getId().equals(eventoId)
                        && i.getStatus() == status);
    }

    @Override
    public long countByEventoId(Long eventoId) {
        return armazenamento.values().stream()
                .filter(i -> i.getEvento().getId().equals(eventoId))
                .count();
    }

    @Override
    public long countByEventoIdAndStatusNot(Long eventoId, StatusInscricao status) {
        return armazenamento.values().stream()
                .filter(i -> i.getEvento().getId().equals(eventoId))
                .filter(i -> i.getStatus() != status)
                .count();
    }

    @Override
    public List<Inscricao> buscarConflitos(String cpf, LocalDateTime inicio, LocalDateTime fim) {
        return armazenamento.values().stream()
                .filter(i -> i.getParticipante().getCpf().equals(cpf))
                .filter(i -> i.getStatus() == StatusInscricao.CONFIRMADA)
                .filter(i -> i.getEvento().getDataHoraInicio().isBefore(fim)
                        && i.getEvento().getDataHoraTermino().isAfter(inicio))
                .collect(Collectors.toList());
    }

    @Override
    public long countByParticipanteCpfAndEventoIdAndStatusIn(String cpf, Long eventoId, List<StatusInscricao> statuses) {
        return armazenamento.values().stream()
                .filter(i -> i.getParticipante().getCpf().equals(cpf))
                .filter(i -> i.getEvento().getId().equals(eventoId))
                .filter(i -> statuses.contains(i.getStatus()))
                .count();
    }

    @Override
    public List<Inscricao> findByEventoIdAndStatusIn(Long eventoId, List<StatusInscricao> statuses) {
        return armazenamento.values().stream()
                .filter(i -> i.getEvento().getId().equals(eventoId))
                .filter(i -> statuses.contains(i.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByParticipanteCpfAndEventoIdAndCupomCodigo(String cpf, Long eventoId, String cupomCodigo) {
        return armazenamento.values().stream()
                .anyMatch(i -> i.getParticipante().getCpf().equals(cpf)
                        && i.getEvento().getId().equals(eventoId)
                        && cupomCodigo.equals(i.getCupomCodigo()));
    }
}
