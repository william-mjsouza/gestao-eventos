package com.gestaoeventos.dominio.evento.evento;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EVENTO")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O nome do evento é obrigatório")
    private String nome;

    @Column(nullable = false)
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @Column(nullable = false)
    @NotNull(message = "A data e horário de início são obrigatórios")
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    @NotNull(message = "A data e horário de término são obrigatórios")
    private LocalDateTime dataHoraFim;

    @Column(nullable = false)
    @NotBlank(message = "O local é obrigatório")
    private String local;

    @Column(nullable = false)
    @Min(value = 1, message = "A capacidade deve ser maior que zero")
    private int capacidade;

    @ManyToOne
    @JoinColumn(name = "organizador_cpf", nullable = false)
    @NotNull(message = "O organizador é obrigatório")
    private Pessoa organizador;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "evento_id")
    private List<Lote> lotes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status = StatusEvento.ATIVO;

    @Transient
    private List<Pessoa> listaEspera = new ArrayList<>();

    public int getTotalVagasDisponiveis() {
        return lotes.stream()
                .mapToInt(Lote::getQuantidadeDisponivel)
                .sum();
    }

    public int getTotalVagasOcupadas() {
        return lotes.stream()
                .mapToInt(lote -> lote.getQuantidadeTotal() - lote.getQuantidadeDisponivel())
                .sum();
    }
    public boolean temVaga() {
        return getTotalVagasOcupadas() < capacidade;
    }

    public boolean capacidadeValida() {
        int totalLotes = lotes.stream()
                .mapToInt(Lote::getQuantidadeTotal)
                .sum();

        return totalLotes <= capacidade;
    }

    public void adicionarNaListaEspera(Pessoa pessoa) {
        listaEspera.add(pessoa);
    }

    public Pessoa proximoDaListaEspera() {
        if (listaEspera.isEmpty()) return null;
        return listaEspera.remove(0); // FIFO
    }

    public boolean temConflitoHorario(LocalDateTime outroInicio, LocalDateTime outroFim) {
        if (this.dataHoraInicio == null || this.dataHoraFim == null || outroInicio == null || outroFim == null) {
            return false;
        }
        return this.dataHoraInicio.isBefore(outroFim) && this.dataHoraFim.isAfter(outroInicio);
    }

    public boolean periodoValido() {
        return dataHoraFim != null && dataHoraInicio != null && dataHoraFim.isAfter(dataHoraInicio);
    }
}
