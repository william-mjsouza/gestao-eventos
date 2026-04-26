package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteRepositorio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class LoteRepositorioImpl implements LoteRepositorio {

    private final EntityManager entityManager;

    public LoteRepositorioImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Lote> buscarPorIdComBloqueio(Long id) {
        Lote lote = entityManager.find(Lote.class, id, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(lote);
    }

    @Override
    public Lote salvar(Lote lote) {
        return entityManager.merge(lote);
    }
}