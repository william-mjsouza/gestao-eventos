package com.gestaoeventos.dominio.inscricao.cupom;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Cupom {

    @NotBlank
    private String codigo;

    @Min(1)
    private int limiteUsoGeral;

    @Min(0)
    private int usosAtuais;

    public Cupom() {}

    public Cupom(String codigo, int limiteUsoGeral, int usosAtuais) {
        this.codigo = codigo;
        this.limiteUsoGeral = limiteUsoGeral;
        this.usosAtuais = usosAtuais;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getLimiteUsoGeral() { return limiteUsoGeral; }
    public void setLimiteUsoGeral(int limiteUsoGeral) { this.limiteUsoGeral = limiteUsoGeral; }

    public int getUsosAtuais() { return usosAtuais; }
    public void setUsosAtuais(int usosAtuais) { this.usosAtuais = usosAtuais; }

    public boolean isEsgotado() {
        return this.usosAtuais >= this.limiteUsoGeral;
    }

    public void registrarUso() {
        if (isEsgotado()) {
            throw new CupomException("Cupom esgotado.");
        }
        this.usosAtuais++;
    }
}
