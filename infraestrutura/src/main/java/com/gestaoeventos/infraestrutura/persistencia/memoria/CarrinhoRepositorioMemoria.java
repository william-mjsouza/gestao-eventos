package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.inscricao.carrinho.Carrinho;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CarrinhoRepositorioMemoria implements CarrinhoRepositorio {

    private final Map<Long, Carrinho> armazenamento = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Optional<Carrinho> findById(Long id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public Carrinho save(Carrinho carrinho) {
        if (carrinho.getId() == null) {
            carrinho.setId(sequencia.getAndIncrement());
        }
        armazenamento.put(carrinho.getId(), carrinho);
        return carrinho;
    }

    @Override
    public void delete(Carrinho carrinho) {
        armazenamento.remove(carrinho.getId());
    }

    @Override
    public boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return armazenamento.values().stream()
                .anyMatch(c -> c.getParticipante().getCpf().equals(cpf)
                        && c.getEvento().getId().equals(eventoId));
    }

    @Override
    public Optional<Carrinho> findByParticipanteCpf(String cpf) {
        return armazenamento.values().stream()
                .filter(c -> c.getParticipante().getCpf().equals(cpf))
                .findFirst();
    }
}
