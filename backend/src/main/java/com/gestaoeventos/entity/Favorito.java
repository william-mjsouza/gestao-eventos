package com.gestaoeventos.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "FAVORITO", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pessoa_cpf", "evento_id"})
})
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pessoa_cpf")
    @JsonIgnoreProperties({"senha", "saldo", "favoritos"})
    private Pessoa pessoa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id")
    @JsonIgnoreProperties({"lotes", "favoritos"})
    private Evento evento;

    private LocalDateTime dataFavoritado = LocalDateTime.now();
}