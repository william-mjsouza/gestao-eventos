package com.gestaoeventos.dominio.inscricao.avaliacao;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class Avaliacao {

    private Long id;

    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private int nota;

    private String comentario;

    @NotNull(message = "O evento é obrigatório para a avaliação")
    private Evento evento;

    @NotNull(message = "O autor da avaliação é obrigatório")
    private Pessoa pessoa;

    public Avaliacao() {}

    public Avaliacao(Long id, int nota, String comentario, Evento evento, Pessoa pessoa) {
        this.id = id;
        this.nota = nota;
        this.comentario = comentario;
        this.evento = evento;
        this.pessoa = pessoa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public Pessoa getPessoa() { return pessoa; }
    public void setPessoa(Pessoa pessoa) { this.pessoa = pessoa; }
}
