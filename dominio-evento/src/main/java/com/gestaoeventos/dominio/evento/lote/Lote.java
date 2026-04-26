package com.gestaoeventos.dominio.evento.lote;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public Lote() {}

    public Lote(Long id, String nome, BigDecimal preco, int quantidadeTotal, int quantidadeDisponivel,
                LocalDateTime dataInicioVenda, LocalDateTime dataFimVenda) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.dataInicioVenda = dataInicioVenda;
        this.dataFimVenda = dataFimVenda;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getQuantidadeTotal() { return quantidadeTotal; }
    public void setQuantidadeTotal(int quantidadeTotal) { this.quantidadeTotal = quantidadeTotal; }

    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) { this.quantidadeDisponivel = quantidadeDisponivel; }

    public LocalDateTime getDataInicioVenda() { return dataInicioVenda; }
    public void setDataInicioVenda(LocalDateTime dataInicioVenda) { this.dataInicioVenda = dataInicioVenda; }

    public LocalDateTime getDataFimVenda() { return dataFimVenda; }
    public void setDataFimVenda(LocalDateTime dataFimVenda) { this.dataFimVenda = dataFimVenda; }

    public boolean temVaga() {
        return quantidadeDisponivel > 0;
    }

    public void ocuparVaga() {
        if (!temVaga()) {
            throw new RuntimeException("Lote esgotado");
        }
        quantidadeDisponivel--;
    }

    public void liberarVaga() {
        if (quantidadeDisponivel < quantidadeTotal) {
            quantidadeDisponivel++;
        }
    }

    public void reservarVaga() {
        if (this.quantidadeDisponivel <= 0) {
            throw new RuntimeException("Não há vagas disponíveis neste lote");
        }
        this.quantidadeDisponivel--;
    }
}
