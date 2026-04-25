package com.gestaoeventos.dominio.evento.evento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepositorio extends JpaRepository<Evento, Long> {
    boolean existsByNome(String nome);
    java.util.Optional<Evento> buscarPorId(Long id);
    void salvar(Evento evento);

    void atualizarVagasLote(Long loteId, int quantidade);

}
