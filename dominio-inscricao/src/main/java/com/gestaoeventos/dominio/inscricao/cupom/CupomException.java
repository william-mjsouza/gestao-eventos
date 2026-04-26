package com.gestaoeventos.dominio.inscricao.cupom;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class CupomException extends GestaoEventoException {
    public CupomException(String message) {
        super(message);
    }
}
