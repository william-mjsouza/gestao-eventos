package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LoteRepositorioMemoria implements LoteRepositorio {

    private final Map<Long, Lote> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Optional<Lote> buscarPorIdComBloqueio(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Lote salvar(Lote lote) {
        if (lote.getId() == null) {
            lote.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(lote.getId(), lote);
        return lote;
    }
}
