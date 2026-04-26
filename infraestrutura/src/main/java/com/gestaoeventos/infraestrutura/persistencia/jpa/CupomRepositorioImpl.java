package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.cupom.Cupom;
import com.gestaoeventos.dominio.inscricao.cupom.CupomRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CupomRepositorioImpl implements CupomRepositorio {

    private final CupomRepositorioJpa jpa;

    public CupomRepositorioImpl(CupomRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Cupom> findById(String codigo) { return jpa.findById(codigo); }

    @Override
    public Cupom save(Cupom cupom) { return jpa.save(cupom); }
}
