package com.gestaoeventos.dominio.inscricao.carrinho;

import com.gestaoeventos.dominio.compartilhado.GestaoEventoException;

public class CarrinhoException extends GestaoEventoException {
    public CarrinhoException(String message) {
        super(message);
    }
}