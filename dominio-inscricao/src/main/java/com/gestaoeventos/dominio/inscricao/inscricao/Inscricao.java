package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "INSCRICAO")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "participante_cpf")
    private Pessoa participante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusInscricao status = StatusInscricao.PENDENTE;

    @Column(nullable = false)
    private LocalDateTime dataReserva = LocalDateTime.now();

    @Column(name = "cupom_codigo")
    private String cupomCodigo;

    public Inscricao(Long id, Pessoa participante, Evento evento, Lote lote,
                     StatusInscricao status, LocalDateTime dataReserva) {
        this.id = id;
        this.participante = participante;
        this.evento = evento;
        this.lote = lote;
        this.status = status;
        this.dataReserva = dataReserva;
    }
}
