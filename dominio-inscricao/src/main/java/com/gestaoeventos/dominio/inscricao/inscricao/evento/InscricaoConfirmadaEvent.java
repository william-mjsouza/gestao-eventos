package com.gestaoeventos.dominio.inscricao.inscricao.evento;

public class InscricaoConfirmadaEvent {

    private final Long inscricaoId;
    private final Long usuarioId;

    public InscricaoConfirmadaEvent(Long inscricaoId, Long usuarioId) {
        this.inscricaoId = inscricaoId;
        this.usuarioId = usuarioId;
    }

    public Long getInscricaoId() {
        return inscricaoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}