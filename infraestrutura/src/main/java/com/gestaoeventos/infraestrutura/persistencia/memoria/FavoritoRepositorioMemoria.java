package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.inscricao.favorito.Favorito;
import com.gestaoeventos.dominio.inscricao.favorito.FavoritoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class FavoritoRepositorioMemoria implements FavoritoRepositorio {

    private final Map<Long, Favorito> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Optional<Favorito> findByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId) {
        return armazenamento.values().stream()
                .filter(f -> f.getPessoa().getCpf().equals(pessoaCpf)
                        && f.getEvento().getId().equals(eventoId))
                .findFirst();
    }

    @Override
    public List<Favorito> findAllByPessoaCpf(String pessoaCpf) {
        return armazenamento.values().stream()
                .filter(f -> f.getPessoa().getCpf().equals(pessoaCpf))
                .collect(Collectors.toList());
    }

    @Override
    public Favorito save(Favorito favorito) {
        if (favorito.getId() == null) {
            favorito.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(favorito.getId(), favorito);
        return favorito;
    }

    @Override
    public void delete(Favorito favorito) {
        armazenamento.remove(favorito.getId());
    }
}
