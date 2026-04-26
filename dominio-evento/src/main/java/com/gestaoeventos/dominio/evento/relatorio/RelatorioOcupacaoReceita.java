package com.gestaoeventos.dominio.evento.relatorio;

import java.math.BigDecimal;

public class RelatorioOcupacaoReceita {

    private Long eventoId;
    private String nomeEvento;
    private int vagasTotais;
    private int vagasOcupadas;
    private BigDecimal receitaTotal;

    public RelatorioOcupacaoReceita(Long eventoId, String nomeEvento,
                                     int vagasTotais, int vagasOcupadas,
                                     BigDecimal receitaTotal) {
        this.eventoId = eventoId;
        this.nomeEvento = nomeEvento;
        this.vagasTotais = vagasTotais;
        this.vagasOcupadas = vagasOcupadas;
        this.receitaTotal = receitaTotal;
    }

    public Long getEventoId() { return eventoId; }
    public String getNomeEvento() { return nomeEvento; }
    public int getVagasTotais() { return vagasTotais; }
    public int getVagasOcupadas() { return vagasOcupadas; }
    public BigDecimal getReceitaTotal() { return receitaTotal; }

    public double getTaxaOcupacao() {
        if (vagasTotais == 0) return 0;
        return (vagasOcupadas * 100.0) / vagasTotais;
    }
}