package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio; // IMPORTANTE
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
import static org.mockito.Mockito.*;

public class PagamentoSteps {

    @Autowired
    private InscricaoServico inscricaoServico;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;


    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Pessoa participante;
    private Evento evento;
    private Lote lote;
    private Inscricao inscricaoPendente;
    private Exception excecao;

    private void inicializarDadosBasicos(Double valorLote) {
        evento = new Evento();
        evento.setId(2L);
        lote = new Lote(1L, "Lote VIP", BigDecimal.valueOf(valorLote), 100, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(10));
        evento.getLotes().add(lote);
    }

    @Dado("que o valor total do carrinho é de R$ {double}")
    public void valor_carrinho(Double valor) {
        inicializarDadosBasicos(valor);
    }

    @E("o participante possui um saldo de R$ {double}")
    @E("o participante possui um saldo de apenas R$ {double}")
    public void participante_possui_saldo(Double saldo) {
        participante = new Pessoa();
        participante.setCpf("99988877766");
        participante.setSaldo(saldo);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        inscricaoPendente = new Inscricao(10L, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        when(inscricaoRepositorio.findById(10L)).thenReturn(Optional.of(inscricaoPendente));
        when(inscricaoRepositorio.save(any(Inscricao.class))).thenReturn(inscricaoPendente);
    }

    @Quando("ele confirmar o pagamento da inscrição via {string}")
    @Quando("ele tentar confirmar o pagamento da inscrição via {string}")
    public void tentar_confirmar_pagamento_via(String metodoPagamento) {
        try {
            TipoPagamento tipoPagamento = TipoPagamento.valueOf(metodoPagamento.toUpperCase());
            inscricaoServico.confirmarPagamento(10L, tipoPagamento);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve debitar R$ {double} da carteira \\(atualizando o saldo para R$ {double})")
    public void sistema_debita_carteira(Double debitado, Double novoSaldo) {
        assertNull(excecao, "Não deveria ocorrer erro ao processar pagamento com saldo suficiente. Detalhe: " + (excecao != null ? excecao.getMessage() : ""));
        assertEquals(novoSaldo, participante.getSaldo(), "O saldo após o débito não está correto.");
    }

    @E("a inscrição deve ser confirmada")
    public void inscricao_confirmada() {
        assertEquals(StatusInscricao.CONFIRMADA, inscricaoPendente.getStatus(),
                "O status da inscrição deveria ser alterado para CONFIRMADA.");
    }

    @Entao("o sistema deve bloquear a transação")
    public void bloquear_transacao() {
        assertNotNull(excecao, "O sistema deveria ter lançado uma exceção por saldo insuficiente.");
        assertEquals(StatusInscricao.PENDENTE, inscricaoPendente.getStatus(),
                "A inscrição deve continuar pendente.");
    }

    @E("exibir uma mensagem de erro informando {string}")
    public void exibir_mensagem_erro_saldo(String mensagemEsperada) {
        assertNotNull(excecao, "Uma exceção era esperada, mas não ocorreu.");
        assertTrue(excecao.getMessage().contains(mensagemEsperada),
                "A mensagem de erro não confere.\nEsperada: " + mensagemEsperada + "\nAtual: " + excecao.getMessage());
    }
}