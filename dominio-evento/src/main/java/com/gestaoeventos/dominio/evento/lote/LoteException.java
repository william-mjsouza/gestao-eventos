package com.gestaoeventos.dominio.evento.lote;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class LoteException extends GestaoEventoException {
    public LoteException(String message) {
        super(message);
    }
}
