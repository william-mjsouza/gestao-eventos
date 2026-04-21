package com.gestaoeventos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LISTA_ESPERA")
public class ListaEspera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "participante_cpf")
    private Pessoa participante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @Column(nullable = false)
    private int posicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusListaEspera status = StatusListaEspera.AGUARDANDO;

    private LocalDateTime dataExpiracaoCarrinho;

    @Column(nullable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now();
}