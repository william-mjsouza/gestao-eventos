package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.carrinho.Carrinho;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CarrinhoRepositorioImpl implements CarrinhoRepositorio {

    private final CarrinhoRepositorioJpa jpa;

    public CarrinhoRepositorioImpl(CarrinhoRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Carrinho> findById(Long id) { return jpa.findById(id); }

    @Override
    public Carrinho save(Carrinho carrinho) { return jpa.save(carrinho); }

    @Override
    public void delete(Carrinho carrinho) { jpa.delete(carrinho); }

    @Override
    public boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId) {
        return jpa.existsByParticipanteCpfAndEventoId(cpf, eventoId);
    }

    @Override
    public Optional<Carrinho> findByParticipanteCpf(String cpf) {
        return jpa.findByParticipanteCpf(cpf);
    }
}
