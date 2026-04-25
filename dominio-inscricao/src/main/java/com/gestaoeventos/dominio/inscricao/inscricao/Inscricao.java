package com.gestaoeventos.dominio.inscricao.inscricao;

import java.time.LocalDateTime;

public class Inscricao {
    private String cpfParticipante;
    private Long eventoId;
    private LocalDateTime dataInscricao;

    public Inscricao(String cpfParticipante, Long eventoId) {
        this.cpfParticipante = cpfParticipante;
        this.eventoId = eventoId;
        this.dataInscricao = LocalDateTime.now();
    }
    

    public String getCpfParticipante() { return cpfParticipante; }
    public Long getEventoId() { return eventoId; }
    public LocalDateTime getDataInscricao() { return dataInscricao; }
}