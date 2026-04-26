package com.gestaoeventos.dominio.inscricao.avaliacao;

import java.util.List;

public interface AvaliacaoRepositorio {
    Avaliacao save(Avaliacao avaliacao);
    List<Avaliacao> findByEventoId(Long eventoId);

    boolean existsByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId);
}