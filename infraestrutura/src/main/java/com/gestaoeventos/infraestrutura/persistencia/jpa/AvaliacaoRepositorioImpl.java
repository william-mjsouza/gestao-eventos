package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.avaliacao.Avaliacao;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AvaliacaoRepositorioImpl implements AvaliacaoRepositorio {

    private final AvaliacaoRepositorioJpa jpa;

    public AvaliacaoRepositorioImpl(AvaliacaoRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Avaliacao save(Avaliacao avaliacao) { return jpa.save(avaliacao); }

    @Override
    public List<Avaliacao> findByEventoId(Long eventoId) { return jpa.findByEventoId(eventoId); }
}
