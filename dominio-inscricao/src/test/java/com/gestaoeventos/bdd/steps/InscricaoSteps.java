package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.CancelamentoInscricaoServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.TipoPagamento;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class InscricaoSteps {

    @Autowired private InscricaoServico inscricaoServico;
    @Autowired private CancelamentoInscricaoServico cancelamentoServico;

    @Autowired private InscricaoRepositorio inscricaoRepositorio;
    @Autowired private PessoaRepositorio pessoaRepositorio;
    @Autowired private EventoRepositorio eventoRepositorio;

    private Pessoa participante;
    private Evento evento;
    private Lote lote;
    private Inscricao inscricaoGerada;
    private Exception excecao;

    private Inscricao inscricaoCancelamento;
    private double saldoInicial;
    private int vagasIniciais;

    @Dado("que o evento está ativo")
    public void evento_ativo() {
        evento = new Evento();
        evento.setId(1L);
        evento.setCapacidade(100);
        when(eventoRepositorio.findById(1L)).thenReturn(Optional.of(evento));
    }

    @E("possui vagas")
    public void possui_vagas() {
        when(inscricaoRepositorio.countByEventoIdAndStatusNot(1L, StatusInscricao.CANCELADA)).thenReturn(50L);

        lote = new Lote(1L, "Lote Padrão", new BigDecimal("100.00"), 100, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        evento.getLotes().add(lote);

        participante = new Pessoa();
        participante.setCpf("11122233344");
        participante.setSaldo(150.0);
        when(pessoaRepositorio.findById("11122233344")).thenReturn(Optional.of(participante));
    }

    @Quando("o participante realiza o pagamento")
    public void participante_realiza_pagamento() {
        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);

        Inscricao inscricaoSimulada = new Inscricao(1L, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        when(inscricaoRepositorio.save(any(Inscricao.class))).thenReturn(inscricaoSimulada);
        when(inscricaoRepositorio.findById(1L)).thenReturn(Optional.of(inscricaoSimulada));

        Inscricao pendente = inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        inscricaoGerada = inscricaoServico.confirmarPagamento(pendente.getId(), TipoPagamento.PIX);
    }

    @Entao("o sistema deve inscrever o participante")
    public void sistema_deve_inscrever_participante() {
        assertNotNull(inscricaoGerada, "A inscrição deveria ter sido gerada.");
        assertEquals(StatusInscricao.CONFIRMADA, inscricaoGerada.getStatus(), "A inscrição deveria estar confirmada.");
    }

    @Dado("que o usuário já está inscrito no evento")
    public void usuario_ja_inscrito() {
        evento_ativo();
        possui_vagas();
        when(inscricaoRepositorio.countByParticipanteCpfAndEventoIdAndStatusIn(
                eq(participante.getCpf()), 
                eq(evento.getId()), 
                anyList()
        )).thenReturn(1L); // Simula que ele já tem 1 ingresso e o limite padrao do evento também é 1
    }

    @Quando("ele tenta iniciar uma nova inscrição para o mesmo evento")
    public void tenta_nova_inscricao() {
        try {
            inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve alertar que ele já possui participação")
    public void sistema_alerta_participacao() {
        assertNotNull(excecao, "Uma exceção deveria ser lançada.");
        assertTrue(excecao instanceof InscricaoException);
        assertTrue(excecao.getMessage().contains("Limite de ingressos por usuário atingido"), "A mensagem deve relatar sobre limite excedido");
    }

    @Dado("que um participante possui uma inscrição confirmada")
    public void participante_possui_inscricao_confirmada() {
        participante = new Pessoa();
        participante.setCpf("98765432100");
        saldoInicial = 500.0;
        participante.setSaldo(saldoInicial);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        evento = new Evento();
        evento.setId(1L);

        lote = new Lote();
        lote.setId(1L);
        lote.setPreco(new BigDecimal("100.00"));
        vagasIniciais = 50;
        lote.setQuantidadeDisponivel(vagasIniciais);

        evento.getLotes().add(lote);

        inscricaoCancelamento = new Inscricao(1L, participante, evento, lote, StatusInscricao.CONFIRMADA, LocalDateTime.now());
        excecao = null;

        when(inscricaoRepositorio.findById(1L)).thenReturn(Optional.of(inscricaoCancelamento));
    }

    @E("o evento está marcado para daqui a {int} dias")
    public void evento_marcado_para_dias(int dias) {
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(dias));
    }

    @E("o evento está marcado para daqui a {int} horas")
    public void evento_marcado_para_horas(int horas) {
        evento.setDataHoraInicio(LocalDateTime.now().plusHours(horas));
    }

    @Quando("ele solicita o cancelamento da inscrição")
    public void solicita_cancelamento() {
        try {
            cancelamentoServico.executar(1L);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a inscrição deve ser cancelada com sucesso")
    public void inscricao_cancelada_com_sucesso() {
        assertNull(excecao, "Não deveria ter ocorrido erro no cancelamento. Detalhe: " + (excecao != null ? excecao.getMessage() : ""));
        assertEquals(StatusInscricao.CANCELADA, inscricaoCancelamento.getStatus());
        verify(inscricaoRepositorio, times(1)).save(inscricaoCancelamento);
    }

    @E("o saldo do participante deve ser estornado")
    public void saldo_estornado() {
        assertEquals(saldoInicial + lote.getPreco().doubleValue(), participante.getSaldo(),
                "O saldo do participante não foi estornado corretamente");
        verify(pessoaRepositorio, times(1)).save(participante);
    }

    @E("a vaga deve voltar para o lote")
    public void vaga_devolvida_ao_lote() {
        assertEquals(vagasIniciais + 1, lote.getQuantidadeDisponivel(),
                "A quantidade disponível no lote não foi incrementada");
        verify(eventoRepositorio, times(1)).save(evento);
    }

    @Entao("o sistema deve rejeitar o cancelamento")
    public void rejeitar_cancelamento() {
        assertNotNull(excecao, "Uma exceção deveria ter sido lançada por estar fora do prazo");
        assertEquals(StatusInscricao.CONFIRMADA, inscricaoCancelamento.getStatus());
    }

    @E("exibir uma mensagem de erro de prazo excedido")
    public void exibir_mensagem_prazo_excedido() {
        assertTrue(excecao instanceof InscricaoException, "Deveria ser uma InscricaoException");
        String mensagemErro = excecao.getMessage().toLowerCase();
        assertTrue(mensagemErro.contains("48 horas"),
                "A mensagem de erro não informou o motivo do bloqueio. Mensagem atual: " + excecao.getMessage());
    }

    private Inscricao inscricaoPendenteAtomicidade;

    @Dado("que o usuário tem saldo suficiente")
    public void usuario_tem_saldo_suficiente() {
        excecao = null;

        participante = new Pessoa();
        participante.setCpf("11122233344");
        saldoInicial = 500.0;
        participante.setSaldo(saldoInicial);
        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        evento = new Evento();
        evento.setId(99L);
        evento.setCapacidade(100);

        lote = new Lote();
        lote.setId(99L);
        lote.setPreco(new BigDecimal("100.00"));
        vagasIniciais = 50;
        lote.setQuantidadeDisponivel(vagasIniciais);

        evento.getLotes().add(lote);
    }

    @E("aciona a finalização da compra")
    public void aciona_finalizacao_da_compra() {
        inscricaoPendenteAtomicidade = new Inscricao(
                77L, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        when(inscricaoRepositorio.findById(77L)).thenReturn(Optional.of(inscricaoPendenteAtomicidade));
    }

    @Quando("o sistema desconta o saldo, mas ocorre uma falha ao salvar a inscrição")
    public void desconta_saldo_mas_falha_salvar_inscricao() {
        when(inscricaoRepositorio.save(any(Inscricao.class)))
                .thenThrow(new RuntimeException("Falha simulada ao persistir inscrição."));

        try {
            inscricaoServico.confirmarPagamento(77L, TipoPagamento.PIX);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve realizar um rollback")
    public void sistema_deve_realizar_rollback() {
        assertNotNull(excecao, "Uma exceção deveria ter sido lançada.");
        assertTrue(excecao instanceof InscricaoException,
                "A exceção deveria ser InscricaoException, mas foi: " + excecao.getClass().getSimpleName());
        assertEquals(StatusInscricao.PENDENTE, inscricaoPendenteAtomicidade.getStatus(),
                "A inscrição não deveria ter sido confirmada após o rollback.");
        assertEquals(vagasIniciais, lote.getQuantidadeDisponivel(),
                "A vaga do lote deveria ter sido devolvida no rollback.");
    }

    @E("o saldo do usuário deve retornar ao valor original")
    public void saldo_retorna_ao_valor_original() {
        assertEquals(saldoInicial, participante.getSaldo(),
                "O saldo do participante deveria ter sido restaurado ao valor original.");
    }
}
