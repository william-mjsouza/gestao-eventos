package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.CancelamentoInscricaoServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CancelamentoProporcionalSteps {

    @Autowired
    private CancelamentoInscricaoServico cancelamentoInscricaoServico;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private ListaEsperaRepositorio listaEsperaRepositorio;

    private Pessoa participante;
    private Evento evento;
    private Lote lote;
    private Inscricao inscricao;
    private double saldoAntes;
    private Exception excecao;

    private static final String CPF = "55566677788";
    private static final Long INSCRICAO_ID = 99L;

    @Dado("que faltam {int} dias para o início do evento")
    public void faltam_dias_para_evento(int dias) {
        excecao = null;
        configurarCenario(LocalDateTime.now().plusDays(dias));
    }

    @Dado("que faltam {int} horas para o início do evento")
    public void faltam_horas_para_evento(int horas) {
        excecao = null;
        configurarCenario(LocalDateTime.now().plusHours(horas));
    }

    @E("o participante pagou R$ {double} pela inscrição")
    public void participante_pagou_valor(double valor) {
        lote.setPreco(BigDecimal.valueOf(valor));
        saldoAntes = participante.getSaldo();
    }

    @Quando("ele solicita o cancelamento pelo sistema")
    public void solicita_cancelamento_pelo_sistema() {
        try {
            cancelamentoInscricaoServico.executar(INSCRICAO_ID);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a inscrição deve ser cancelada e a vaga liberada")
    public void inscricao_cancelada_e_vaga_liberada() {
        assertNull(excecao, "Não deveria ter ocorrido erro: " + (excecao != null ? excecao.getMessage() : ""));
        assertEquals(StatusInscricao.CANCELADA, inscricao.getStatus());
        verify(inscricaoRepositorio, times(1)).save(inscricao);
        verify(eventoRepositorio, times(1)).save(evento);
    }

    @E("o sistema deve creditar R$ {double} na carteira virtual do usuário")
    public void sistema_credita_valor_na_carteira(double valorEsperado) {
        assertEquals(saldoAntes + valorEsperado, participante.getSaldo(), 0.001,
                "O valor creditado na carteira não corresponde ao estorno esperado");
        verify(pessoaRepositorio, times(1)).save(participante);
    }

    @Entao("o sistema deve rejeitar o cancelamento por prazo insuficiente")
    public void rejeitar_cancelamento_por_prazo() {
        assertNotNull(excecao, "O sistema deveria ter rejeitado o cancelamento.");
        assertTrue(excecao instanceof InscricaoException);
        assertTrue(excecao.getMessage().contains("48 horas"),
                "A mensagem deveria mencionar o prazo de 48 horas. Mensagem: " + excecao.getMessage());
        assertEquals(StatusInscricao.CONFIRMADA, inscricao.getStatus());
        verify(inscricaoRepositorio, never()).save(any());
    }

    private void configurarCenario(LocalDateTime dataEvento) {
        participante = new Pessoa();
        participante.setCpf(CPF);
        participante.setNome("Participante Proporcional");
        participante.setSaldo(300.0);

        lote = new Lote();
        lote.setId(1L);
        lote.setQuantidadeDisponivel(10);

        evento = new Evento();
        evento.setId(1L);
        evento.setDataHoraInicio(dataEvento);
        evento.getLotes().add(lote);

        inscricao = new Inscricao(INSCRICAO_ID, participante, evento, lote,
                StatusInscricao.CONFIRMADA, LocalDateTime.now().minusDays(2));

        when(inscricaoRepositorio.findById(INSCRICAO_ID)).thenReturn(Optional.of(inscricao));
        when(pessoaRepositorio.findById(CPF)).thenReturn(Optional.of(participante));
        when(eventoRepositorio.save(any(Evento.class))).thenAnswer(i -> i.getArgument(0));
        when(inscricaoRepositorio.save(any(Inscricao.class))).thenAnswer(i -> i.getArgument(0));
        when(listaEsperaRepositorio.findByEventoIdAndStatusOrderByPosicaoAsc(anyLong(), any()))
                .thenReturn(java.util.Collections.emptyList());
    }
}
