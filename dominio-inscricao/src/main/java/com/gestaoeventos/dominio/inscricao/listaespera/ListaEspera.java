package com.gestaoeventos.dominio.inscricao.listaespera;

import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;

import java.time.LocalDateTime;

public class ListaEspera {

    private Long id;

    private Pessoa participante;

    private Evento evento;

    private int posicao;

    private StatusListaEspera status = StatusListaEspera.AGUARDANDO;

    private LocalDateTime dataExpiracaoCarrinho;

    public ListaEspera() {}

    public ListaEspera(Long id, Pessoa participante, Evento evento, int posicao,
                       StatusListaEspera status, LocalDateTime dataExpiracaoCarrinho) {
        this.id = id;
        this.participante = participante;
        this.evento = evento;
        this.posicao = posicao;
        this.status = status;
        this.dataExpiracaoCarrinho = dataExpiracaoCarrinho;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pessoa getParticipante() { return participante; }
    public void setParticipante(Pessoa participante) { this.participante = participante; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public int getPosicao() { return posicao; }
    public void setPosicao(int posicao) { this.posicao = posicao; }

    public StatusListaEspera getStatus() { return status; }
    public void setStatus(StatusListaEspera status) { this.status = status; }

    public LocalDateTime getDataExpiracaoCarrinho() { return dataExpiracaoCarrinho; }
    public void setDataExpiracaoCarrinho(LocalDateTime dataExpiracaoCarrinho) {
        this.dataExpiracaoCarrinho = dataExpiracaoCarrinho;
    }
}
