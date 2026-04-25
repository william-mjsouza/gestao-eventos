package com.gestaoeventos.dominio.inscricao.cupom;

public class Cupom {
    private String codigo;
    private int limiteUsoGeral;
    private int usosAtuais;

    public Cupom(String codigo, int limiteUsoGeral, int usosAtuais) {
        this.codigo = codigo;
        this.limiteUsoGeral = limiteUsoGeral;
        this.usosAtuais = usosAtuais;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isEsgotado() {
        return this.usosAtuais >= this.limiteUsoGeral;
    }

    public void registrarUso() {
        if (isEsgotado()) {
            throw new CupomException("Cupom esgotado");
        }
        this.usosAtuais++;
    }
}