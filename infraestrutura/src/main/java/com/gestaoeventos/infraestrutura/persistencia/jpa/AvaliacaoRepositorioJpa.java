package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.avaliacao.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepositorioJpa extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByEventoId(Long eventoId);
    boolean existsByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId);
}
