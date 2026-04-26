package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;

import java.time.LocalDateTime;

public class Inscricao {

    private Long id;

    private Pessoa participante;

    private Evento evento;

    private Lote lote;

    private StatusInscricao status = StatusInscricao.PENDENTE;

    private LocalDateTime dataReserva = LocalDateTime.now();

    private String cupomCodigo;

    public Inscricao() {}

    public Inscricao(Long id, Pessoa participante, Evento evento, Lote lote,
                     StatusInscricao status, LocalDateTime dataReserva) {
        this.id = id;
        this.participante = participante;
        this.evento = evento;
        this.lote = lote;
        this.status = status;
        this.dataReserva = dataReserva;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pessoa getParticipante() { return participante; }
    public void setParticipante(Pessoa participante) { this.participante = participante; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }

    public StatusInscricao getStatus() { return status; }
    public void setStatus(StatusInscricao status) { this.status = status; }

    public LocalDateTime getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDateTime dataReserva) { this.dataReserva = dataReserva; }

    public String getCupomCodigo() { return cupomCodigo; }
    public void setCupomCodigo(String cupomCodigo) { this.cupomCodigo = cupomCodigo; }
}
