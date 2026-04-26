package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.favorito.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepositorioJpa extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId);
    List<Favorito> findAllByPessoaCpf(String pessoaCpf);
}
