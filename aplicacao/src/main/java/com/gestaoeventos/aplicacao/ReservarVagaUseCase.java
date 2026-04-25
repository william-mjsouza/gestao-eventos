package com.gestaoeventos.aplicacao;

import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteRepositorio;

public class ReservarVagaUseCase {

    private final LoteRepositorio loteRepositorio;

    public ReservarVagaUseCase(LoteRepositorio loteRepositorio) {
        this.loteRepositorio = loteRepositorio;
    }

    public void executar(Long loteId) {
        Lote lote = loteRepositorio.buscarPorIdComBloqueio(loteId)
                .orElseThrow(() -> new RuntimeException("Lote não encontrado"));

        lote.reservarVaga();

        loteRepositorio.salvar(lote);
    }
}