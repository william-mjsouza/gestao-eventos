package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.entity.*;
import com.gestaoeventos.exception.ListaEsperaException;
import com.gestaoeventos.repository.*;
import com.gestaoeventos.service.ListaEsperaService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ListaEsperaSteps {

    @Autowired
    private ListaEsperaService listaEsperaService;

    @Autowired
    private ListaEsperaRepository listaEsperaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    private Pessoa primeiroDaFila;
    private Pessoa segundoDaFila;
    private Evento evento;
    private Lote lote;
    private ListaEspera entradaPrimeiro;
    private ListaEspera entradaSegundo;
    private ListaEspera resultado;
    private Exception excecao;


    @Dado("que uma vaga foi liberada no evento lotado")
    public void vaga_liberada_no_evento_lotado() {
        excecao = null;
        resultado = null;


        lote = new Lote();
        lote.setId(1L);
        lote.setNome("Lote Padrão");
        lote.setPreco(new BigDecimal("100.00"));
        lote.setQuantidadeTotal(50);
        lote.setQuantidadeDisponivel(0); // Lotado
        lote.setDataInicioVenda(LocalDateTime.now().minusDays(5));
        lote.setDataFimVenda(LocalDateTime.now().plusDays(5));

        evento = new Evento();
        evento.setId(1L);
        evento.setNome("Tech Summit 2026");
        evento.setDescricao("Evento de tecnologia");
        evento.setLocal("Centro de Convenções");
        evento.setCapacidade(50);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(10));
        evento.getLotes().add(lote);

        when(eventoRepository.findById(evento.getId()))
                .thenReturn(Optional.of(evento));
    }

    @E("o ingresso foi adicionado automaticamente ao carrinho do primeiro usuário da lista de espera")
    public void ingresso_adicionado_ao_carrinho_do_primeiro() {
        primeiroDaFila = new Pessoa();
        primeiroDaFila.setCpf("11122233344");
        primeiroDaFila.setNome("Ana Lima");
        primeiroDaFila.setEmail("ana@email.com");
        primeiroDaFila.setSenha("senha123");
        primeiroDaFila.setSaldo(300.0); // Saldo suficiente
        primeiroDaFila.setOrganizador(false);

        when(pessoaRepository.findById(primeiroDaFila.getCpf()))
                .thenReturn(Optional.of(primeiroDaFila));

        entradaPrimeiro = new ListaEspera();
        entradaPrimeiro.setId(1L);
        entradaPrimeiro.setParticipante(primeiroDaFila);
        entradaPrimeiro.setEvento(evento);
        entradaPrimeiro.setPosicao(1);
        entradaPrimeiro.setStatus(StatusListaEspera.CARRINHO);
        // Prazo expira daqui a 1 hora (dentro do limite)
        entradaPrimeiro.setDataExpiracaoCarrinho(LocalDateTime.now().plusHours(1));

        when(listaEsperaRepository.findByParticipanteCpfAndEventoId(
                primeiroDaFila.getCpf(), evento.getId()))
                .thenReturn(Optional.of(entradaPrimeiro));

        when(listaEsperaRepository.save(any(ListaEspera.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @E("o usuário recebeu a notificação de aviso")
    public void usuario_recebeu_notificacao() {


        assertNotNull(entradaPrimeiro.getDataExpiracaoCarrinho(),
                "A data de expiração deveria ter sido definida ao alocar o ingresso no carrinho.");
    }

    @Quando("ele acessa o carrinho")
    public void ele_acessa_o_carrinho() {

        assertEquals(StatusListaEspera.CARRINHO, entradaPrimeiro.getStatus(),
                "O ingresso deveria estar no carrinho do usuário.");
    }

    @E("finaliza o pagamento dentro do tempo limite")
    public void finaliza_pagamento_dentro_do_prazo() {
        try {
            resultado = listaEsperaService.confirmarPagamento(
                    primeiroDaFila.getCpf(), evento.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a inscrição da lista de espera deve ser confirmada")
    public void inscricao_deve_ser_confirmada() {
        assertNull(excecao, "Não deveria ter ocorrido erro: "
                + (excecao != null ? excecao.getMessage() : ""));
        assertEquals(StatusListaEspera.CONFIRMADO, entradaPrimeiro.getStatus(),
                "O status deveria ser CONFIRMADO após o pagamento.");
        verify(listaEsperaRepository, atLeastOnce()).save(entradaPrimeiro);
    }

    @E("o usuário deve ser removido da lista de espera")
    public void usuario_removido_da_lista() {

        assertNotEquals(StatusListaEspera.AGUARDANDO, entradaPrimeiro.getStatus(),
                "O usuário não deveria mais estar com status AGUARDANDO.");
        assertNotEquals(StatusListaEspera.CARRINHO, entradaPrimeiro.getStatus(),
                "O usuário não deveria mais estar com status CARRINHO.");
    }


    @Dado("que o ingresso foi alocado no carrinho do primeiro usuário da lista de espera")
    public void ingresso_alocado_no_carrinho() {
        excecao = null;
        resultado = null;

        lote = new Lote();
        lote.setId(1L);
        lote.setNome("Lote Padrão");
        lote.setPreco(new BigDecimal("100.00"));
        lote.setQuantidadeTotal(50);
        lote.setQuantidadeDisponivel(0);
        lote.setDataInicioVenda(LocalDateTime.now().minusDays(5));
        lote.setDataFimVenda(LocalDateTime.now().plusDays(5));

        evento = new Evento();
        evento.setId(2L);
        evento.setNome("Design Week 2026");
        evento.setDescricao("Evento de design");
        evento.setLocal("Auditório Central");
        evento.setCapacidade(50);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(15));
        evento.getLotes().add(lote);

        when(eventoRepository.findById(evento.getId()))
                .thenReturn(Optional.of(evento));

        primeiroDaFila = new Pessoa();
        primeiroDaFila.setCpf("99988877766");
        primeiroDaFila.setNome("Bruno Costa");
        primeiroDaFila.setEmail("bruno@email.com");
        primeiroDaFila.setSenha("senha456");
        primeiroDaFila.setSaldo(50.0);
        primeiroDaFila.setOrganizador(false);

        entradaPrimeiro = new ListaEspera();
        entradaPrimeiro.setId(1L);
        entradaPrimeiro.setParticipante(primeiroDaFila);
        entradaPrimeiro.setEvento(evento);
        entradaPrimeiro.setPosicao(1);
        entradaPrimeiro.setStatus(StatusListaEspera.CARRINHO);

        entradaPrimeiro.setDataExpiracaoCarrinho(LocalDateTime.now().minusHours(3));

        when(listaEsperaRepository.findByParticipanteCpfAndEventoId(
                primeiroDaFila.getCpf(), evento.getId()))
                .thenReturn(Optional.of(entradaPrimeiro));

        segundoDaFila = new Pessoa();
        segundoDaFila.setCpf("55566677788");
        segundoDaFila.setNome("Carla Dias");
        segundoDaFila.setEmail("carla@email.com");
        segundoDaFila.setSenha("senha789");
        segundoDaFila.setSaldo(200.0);
        segundoDaFila.setOrganizador(false);

        when(pessoaRepository.findById(segundoDaFila.getCpf()))
                .thenReturn(Optional.of(segundoDaFila));

        entradaSegundo = new ListaEspera();
        entradaSegundo.setId(2L);
        entradaSegundo.setParticipante(segundoDaFila);
        entradaSegundo.setEvento(evento);
        entradaSegundo.setPosicao(2);
        entradaSegundo.setStatus(StatusListaEspera.AGUARDANDO);


        when(listaEsperaRepository.findByEventoIdAndStatusOrderByPosicaoAsc(
                evento.getId(), StatusListaEspera.AGUARDANDO))
                .thenReturn(List.of(entradaSegundo));

        when(listaEsperaRepository.save(any(ListaEspera.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @E("o tempo limite para pagamento era de {int} horas")
    public void tempo_limite_era_de_horas(int horas) {
        assertEquals(ListaEsperaService.HORAS_LIMITE_PAGAMENTO, horas,
                "O tempo limite configurado no sistema deveria ser de " + horas + " horas.");
        assertTrue(entradaPrimeiro.getDataExpiracaoCarrinho().isBefore(LocalDateTime.now()),
                "O prazo do primeiro usuário deveria já ter expirado.");
    }

    @Quando("não realiza o pagamento dentro do tempo limite")
    public void nao_realiza_pagamento_no_prazo() {
        try {
            listaEsperaService.confirmarPagamento(primeiroDaFila.getCpf(), evento.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve remover o ingresso do carrinho desse usuário")
    public void sistema_remove_ingresso_do_carrinho() {
        assertNotNull(excecao, "Deveria ter lançado exceção por prazo expirado.");
        assertTrue(excecao instanceof ListaEsperaException,
                "A exceção deveria ser uma ListaEsperaException.");
        assertEquals(StatusListaEspera.EXPIRADO, entradaPrimeiro.getStatus(),
                "O status do primeiro deveria ser EXPIRADO.");
    }

    @E("adicionar o ingresso no carrinho do próximo participante da fila")
    public void ingresso_repassado_para_proximo() {
        assertEquals(StatusListaEspera.CARRINHO, entradaSegundo.getStatus(),
                "O segundo da fila deveria ter recebido o ingresso no carrinho.");
        assertNotNull(entradaSegundo.getDataExpiracaoCarrinho(),
                "O segundo da fila deveria ter recebido um prazo de pagamento.");
        verify(listaEsperaRepository, atLeast(2)).save(any(ListaEspera.class));
    }
}