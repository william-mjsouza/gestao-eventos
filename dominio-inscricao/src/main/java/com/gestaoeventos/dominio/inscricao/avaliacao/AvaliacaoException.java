package com.gestaoeventos.dominio.inscricao.avaliacao;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class AvaliacaoException extends GestaoEventoException {
    public AvaliacaoException(String mensagem) {
        super(mensagem);
    }
}
