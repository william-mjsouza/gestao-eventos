package com.gestaoeventos.dominio.inscricao.avaliacao;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AVALIACAO")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    @Column(nullable = false)
    private int nota;

    private String comentario;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    @NotNull(message = "O evento é obrigatório para a avaliação")
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "pessoa_cpf", nullable = false)
    @NotNull(message = "O autor da avaliação é obrigatório")
    private Pessoa pessoa;
}
