package com.gestaoeventos.dominio.evento.lote;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOTE")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O nome do lote é obrigatório")
    private String nome;

    @Column(nullable = false)
    @NotNull(message = "O preço é obrigatório")
    @Min(value = 0, message = "O preço não pode ser negativo")
    private BigDecimal preco;

    @Column(nullable = false)
    @Min(value = 1, message = "A quantidade total deve ser pelo menos 1")
    private int quantidadeTotal;

    @Column(nullable = false)
    private int quantidadeDisponivel;

    @Column(nullable = false)
    @NotNull(message = "A data de início das vendas é obrigatória")
    private LocalDateTime dataInicioVenda;

    @Column(nullable = false)
    @NotNull(message = "A data de fim das vendas é obrigatória")
    private LocalDateTime dataFimVenda;

    public void reservarVaga() {
        if (this.quantidadeDisponivel <= 0) {
            throw new RuntimeException("Não há vagas disponíveis neste lote");
        }
        this.quantidadeDisponivel--;
    }
}
