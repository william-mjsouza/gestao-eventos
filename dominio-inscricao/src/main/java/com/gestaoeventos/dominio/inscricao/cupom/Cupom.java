package com.gestaoeventos.dominio.inscricao.cupom;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "CUPOM")
public class Cupom {

    @Id
    @NotBlank
    @Column(name = "codigo", nullable = false, updatable = false)
    private String codigo;

    @Min(1)
    @Column(name = "limite_uso_geral", nullable = false)
    private int limiteUsoGeral;

    @Min(0)
    @Column(name = "usos_atuais", nullable = false)
    private int usosAtuais;

    public Cupom() {
    }

    public Cupom(String codigo, int limiteUsoGeral, int usosAtuais) {
        this.codigo = codigo;
        this.limiteUsoGeral = limiteUsoGeral;
        this.usosAtuais = usosAtuais;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getLimiteUsoGeral() {
        return limiteUsoGeral;
    }

    public void setLimiteUsoGeral(int limiteUsoGeral) {
        this.limiteUsoGeral = limiteUsoGeral;
    }

    public int getUsosAtuais() {
        return usosAtuais;
    }

    public void setUsosAtuais(int usosAtuais) {
        this.usosAtuais = usosAtuais;
    }

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
