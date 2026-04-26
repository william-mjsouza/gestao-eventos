package com.gestaoeventos.dominio.inscricao.favorito;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepositorio {
    Optional<Favorito> findByPessoaCpfAndEventoId(String pessoaCpf, Long eventoId);
    List<Favorito> findAllByPessoaCpf(String pessoaCpf);
    Favorito save(Favorito favorito);
    void delete(Favorito favorito);
}
