package com.gestaoeventos.dominio.inscricao.carrinho;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CARRINHO")
public class Carrinho {

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

    @Column(nullable = false)
    private LocalDateTime dataAdicao = LocalDateTime.now();
}