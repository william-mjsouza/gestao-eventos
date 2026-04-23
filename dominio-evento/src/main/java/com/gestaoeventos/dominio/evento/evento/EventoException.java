package com.gestaoeventos.dominio.evento.evento;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class EventoException extends GestaoEventoException {
    public EventoException(String message) {
        super(message);
    }
}
