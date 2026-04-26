package com.gestaoeventos.dominio.evento.evento;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private LocalDateTime dataHoraTermino;

    @Column(nullable = false)
    @NotBlank(message = "O local é obrigatório")
    private String local;

    @Column(nullable = false)
    @Min(value = 1, message = "A capacidade deve ser maior que zero")
    private int capacidade;

    @Column(nullable = false)
    @Min(value = 1, message = "O limite de ingressos por CPF deve ser maior que zero")
    private int limiteIngressosPorCpf = 1;

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

    @Column(nullable = false)
    @Min(value = 0, message = "A idade mínima não pode ser negativa")
    private Integer idadeMinima = 0;

    public Evento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }

    public LocalDateTime getDataHoraTermino() { return dataHoraTermino; }
    public void setDataHoraTermino(LocalDateTime dataHoraTermino) { this.dataHoraTermino = dataHoraTermino; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    public int getLimiteIngressosPorCpf() { return limiteIngressosPorCpf; }
    public void setLimiteIngressosPorCpf(int limiteIngressosPorCpf) { this.limiteIngressosPorCpf = limiteIngressosPorCpf; }

    public Pessoa getOrganizador() { return organizador; }
    public void setOrganizador(Pessoa organizador) { this.organizador = organizador; }

    public List<Lote> getLotes() { return lotes; }
    public void setLotes(List<Lote> lotes) { this.lotes = lotes; }

    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }

    public List<Pessoa> getListaEspera() { return listaEspera; }
    public void setListaEspera(List<Pessoa> listaEspera) { this.listaEspera = listaEspera; }

    public Integer getIdadeMinima() { return idadeMinima; }
    public void setIdadeMinima(Integer idadeMinima) { this.idadeMinima = idadeMinima; }

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
        return listaEspera.remove(0);
    }

    public boolean temConflitoHorario(LocalDateTime outroInicio, LocalDateTime outroFim) {
        if (this.dataHoraInicio == null || this.dataHoraTermino == null || outroInicio == null || outroFim == null) {
            return false;
        }
        return this.dataHoraInicio.isBefore(outroFim) && this.dataHoraTermino.isAfter(outroInicio);
    }

    public boolean periodoValido() {
        return dataHoraTermino != null && dataHoraInicio != null && dataHoraTermino.isAfter(dataHoraInicio);
    }
}
