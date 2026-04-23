package com.gestaoeventos.dominio.participante.pessoa;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class ParticipanteException extends GestaoEventoException {
    public ParticipanteException(String message) {
        super(message);
    }
}
