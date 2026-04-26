package com.gestaoeventos.dominio.inscricao.inscricao.evento;

public class VagaLiberadaFilaEvent {

    private final Long usuarioId;
    private final Long loteId;

    public VagaLiberadaFilaEvent(Long usuarioId, Long loteId) {
        this.usuarioId = usuarioId;
        this.loteId = loteId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getLoteId() {
        return loteId;
    }
}