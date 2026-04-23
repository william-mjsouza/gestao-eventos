package com.gestaoeventos.dominio.inscricao.listaespera;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class ListaEsperaException extends GestaoEventoException {
    public ListaEsperaException(String message) {
        super(message);
    }
}
