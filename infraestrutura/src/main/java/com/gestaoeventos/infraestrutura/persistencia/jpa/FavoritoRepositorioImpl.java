package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.favorito.Favorito;
import com.gestaoeventos.dominio.inscricao.favorito.FavoritoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FavoritoRepositorioImpl implements FavoritoRepositorio {

    private final FavoritoRepositorioJpa jpa;

    public FavoritoRepositorioImpl(FavoritoRepositorioJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Favorito> findByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId) {
        return jpa.findByPessoaCpfAndEventoId(pessoaCpf, eventoId);
    }

    @Override
    public List<Favorito> findAllByPessoaCpf(String pessoaCpf) {
        return jpa.findAllByPessoaCpf(pessoaCpf);
    }

    @Override
    public Favorito save(Favorito favorito) { return jpa.save(favorito); }

    @Override
    public void delete(Favorito favorito) { jpa.delete(favorito); }
}
