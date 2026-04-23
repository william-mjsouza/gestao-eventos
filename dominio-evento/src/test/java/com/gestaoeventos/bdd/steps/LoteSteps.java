package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoteSteps {

    @Autowired
    private LoteServico loteServico;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    private Evento evento;
    private Lote lote1;
    private Lote lote2;
    private Lote loteRecebido;

    @Dado("que o Lote 1 está ativo")
    public void lote_1_ativo() {
        evento = new Evento();
        evento.setId(10L);
        evento.setLotes(new ArrayList<>());

        lote1 = new Lote();
        lote1.setId(1L);
        lote1.setNome("Lote 1");
        lote1.setPreco(new BigDecimal("50.00"));
        lote1.setDataInicioVenda(LocalDateTime.now().minusDays(5));
        lote1.setDataFimVenda(LocalDateTime.now().plusDays(5));

        evento.getLotes().add(lote1);
        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
    }

    @E("com vagas")
    public void com_vagas() {
        lote1.setQuantidadeTotal(100);
        lote1.setQuantidadeDisponivel(50);
    }

    @Quando("o participante seleciona o ingresso")
    public void participante_seleciona_ingresso() {
        loteRecebido = loteServico.obterLoteAtivo(evento.getId());
    }

    @Entao("o sistema deve aplicar o valor correspondente ao Lote 1")
    public void sistema_deve_aplicar_valor_lote1() {
        assertNotNull(loteRecebido, "O sistema deveria ter retornado um lote ativo");
        assertEquals(lote1.getId(), loteRecebido.getId(), "O lote ativo deveria ser o Lote 1");
        assertEquals(new BigDecimal("50.00"), loteRecebido.getPreco(), "O preço deveria ser do Lote 1");
    }

    @Dado("que a data de validade do Lote 1 expirou ontem")
    public void data_validade_lote1_expirou_ontem() {
        evento = new Evento();
        evento.setId(20L);
        evento.setLotes(new ArrayList<>());

        lote1 = new Lote();
        lote1.setId(1L);
        lote1.setNome("Lote 1");
        lote1.setQuantidadeDisponivel(50);
        lote1.setDataInicioVenda(LocalDateTime.now().minusDays(10));
        lote1.setDataFimVenda(LocalDateTime.now().minusDays(1));

        lote2 = new Lote();
        lote2.setId(2L);
        lote2.setNome("Lote 2");
        lote2.setPreco(new BigDecimal("80.00"));
        lote2.setQuantidadeDisponivel(100);
        lote2.setDataInicioVenda(LocalDateTime.now().minusHours(2));
        lote2.setDataFimVenda(LocalDateTime.now().plusDays(10));

        evento.getLotes().add(lote1);
        evento.getLotes().add(lote2);

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
    }

    @Quando("o usuário acessa a página do evento hoje")
    public void usuario_acessa_pagina_hoje() {
        loteRecebido = loteServico.obterLoteAtivo(evento.getId());
    }

    @Entao("o Lote 1 deve constar como indisponível")
    public void lote1_indisponivel() {
        assertNotNull(loteRecebido);
        assertNotEquals(lote1.getId(), loteRecebido.getId(), "O Lote 1 não deveria estar ativo");
    }

    @E("o Lote 2 deve ser oferecido")
    public void lote2_oferecido() {
        assertEquals(lote2.getId(), loteRecebido.getId(), "O Lote 2 deveria ser o lote retornado");
        assertEquals(new BigDecimal("80.00"), loteRecebido.getPreco());
    }
}
