package com.gestaoeventos.dominio.inscricao.carrinho;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;

import java.time.LocalDateTime;

public class Carrinho {

    private Long id;

    private Pessoa participante;

    private Evento evento;

    private Lote lote;

    private LocalDateTime dataAdicao = LocalDateTime.now();

    public Carrinho() {}

    public Carrinho(Long id, Pessoa participante, Evento evento, Lote lote, LocalDateTime dataAdicao) {
        this.id = id;
        this.participante = participante;
        this.evento = evento;
        this.lote = lote;
        this.dataAdicao = dataAdicao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pessoa getParticipante() { return participante; }
    public void setParticipante(Pessoa participante) { this.participante = participante; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public LocalDateTime getDataAdicao() { return dataAdicao; }
    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }
}
