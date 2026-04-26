package com.gestaoeventos.dominio.evento.relatorio;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class RelatorioException extends GestaoEventoException {
    public RelatorioException(String message) {
        super(message);
    }
}