package com.gestaoeventos.repository;

import com.gestaoeventos.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId);
    List<Favorito> findAllByPessoaCpf(String pessoaCpf);
}