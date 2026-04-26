package com.gestaoeventos.dominio.inscricao.avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepositorio extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByEventoId(Long eventoId);

    boolean existsByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId);
}