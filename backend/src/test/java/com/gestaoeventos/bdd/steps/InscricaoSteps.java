package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.entity.*;
import com.gestaoeventos.exception.InscricaoException;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.InscricaoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import com.gestaoeventos.service.InscricaoService;
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
import static org.mockito.Mockito.*;

public class InscricaoSteps {

    @Autowired
    private InscricaoService inscricaoService;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    private Pessoa participante;
    private Evento evento;
    private Lote lote;
    private Inscricao inscricaoGerada;
    private Exception excecao;

    @Dado("que o evento está ativo")
    public void evento_ativo() {
        evento = new Evento();
        evento.setId(1L);
        evento.setCapacidade(100);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
    }

    @E("possui vagas")
    public void possui_vagas() {
        when(inscricaoRepository.countByEventoId(1L)).thenReturn(50L);

        lote = new Lote(1L, "Lote Padrão", new BigDecimal("100.00"), 100, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        evento.getLotes().add(lote);

        participante = new Pessoa();
        participante.setCpf("11122233344");
        participante.setSaldo(150.0);
        when(pessoaRepository.findById("11122233344")).thenReturn(Optional.of(participante));
    }

    @Quando("o participante realiza o pagamento")
    public void participante_realiza_pagamento() {
        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);

        Inscricao inscricaoSimulada = new Inscricao(1L, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(inscricaoSimulada);
        when(inscricaoRepository.findById(1L)).thenReturn(Optional.of(inscricaoSimulada));

        Inscricao pendente = inscricaoService.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        inscricaoGerada = inscricaoService.confirmarPagamento(pendente.getId());
    }

    @Entao("o sistema deve inscrever o participante")
    public void sistema_deve_inscrever_participante() {
        assertNotNull(inscricaoGerada, "A inscrição deveria ter sido gerada.");
        assertEquals(StatusInscricao.CONFIRMADA, inscricaoGerada.getStatus(), "A inscrição deveria estar confirmada após o pagamento.");
    }

    @Dado("que o usuário já está inscrito no evento")
    public void usuario_ja_inscrito() {
        evento_ativo();
        possui_vagas();

        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(participante.getCpf(), evento.getId())).thenReturn(true);
    }

    @Quando("ele tenta iniciar uma nova inscrição para o mesmo evento")
    public void tenta_nova_inscricao() {
        try {
            inscricaoService.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve alertar que ele já possui participação")
    public void sistema_alerta_participacao() {
        assertNotNull(excecao, "Uma exceção deveria ser lançada para inscrição duplicada.");
        assertTrue(excecao instanceof InscricaoException, "A exceção deveria ser do tipo InscricaoException");
        assertEquals("Usuário já possui participação neste evento.", excecao.getMessage());
    }
}