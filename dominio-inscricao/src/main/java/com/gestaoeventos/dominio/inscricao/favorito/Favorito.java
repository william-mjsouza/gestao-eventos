package com.gestaoeventos.dominio.inscricao.favorito;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;

import java.time.LocalDateTime;

public class Favorito {

    private Long id;

    private Pessoa pessoa;

    private Evento evento;

    private LocalDateTime dataFavoritado = LocalDateTime.now();

    public Favorito() {}

    public Favorito(Long id, Pessoa pessoa, Evento evento, LocalDateTime dataFavoritado) {
        this.id = id;
        this.pessoa = pessoa;
        this.evento = evento;
        this.dataFavoritado = dataFavoritado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pessoa getPessoa() { return pessoa; }
    public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public LocalDateTime getDataFavoritado() { return dataFavoritado; }
    public void setDataFavoritado(LocalDateTime dataFavoritado) { this.dataFavoritado = dataFavoritado; }
}
