package com.gestaoeventos.dominio.evento.lote;

import java.util.Optional;

public interface LoteRepositorio {

    Optional<Lote> buscarPorIdComBloqueio(Long id);

    Lote salvar(Lote lote);
}