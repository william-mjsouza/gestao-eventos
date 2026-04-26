package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.inscricao.avaliacao.Avaliacao;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class AvaliacaoRepositorioMemoria implements AvaliacaoRepositorio {

    private final Map<Long, Avaliacao> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        if (avaliacao.getId() == null) {
            avaliacao.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(avaliacao.getId(), avaliacao);
        return avaliacao;
    }

    @Override
    public List<Avaliacao> findByEventoId(Long eventoId) {
        return armazenamento.values().stream()
                .filter(a -> a.getEvento().getId().equals(eventoId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId) {
        return armazenamento.values().stream()
                .anyMatch(a -> a.getPessoa().getCpf().equals(pessoaCpf)
                        && a.getEvento().getId().equals(eventoId));
    }
}
