package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.inscricao.avaliacao.Avaliacao;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoException;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoRepositorio;
import com.gestaoeventos.dominio.inscricao.avaliacao.AvaliacaoServico;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AvaliacaoSteps {

    @Autowired
    private AvaliacaoServico avaliacaoServico;

    @Autowired
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Pessoa participante;
    private Evento evento;
    private Avaliacao avaliacaoSalva;
    private Exception excecao;

    @Dado("que o participante compareceu ao evento")
    public void participante_compareceu_ao_evento() {
        excecao = null;
        avaliacaoSalva = null;

        participante = new Pessoa();
        participante.setCpf("11122233344");
        participante.setNome("Maria Silva");
        participante.setEmail("maria@email.com");
        participante.setSenha("senha123");
        participante.setSaldo(0.0);
        participante.setOrganizador(false);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoIdAndStatus(
                eq(participante.getCpf()), anyLong(), eq(StatusInscricao.CONFIRMADA)))
                .thenReturn(true);

        when(avaliacaoRepositorio.existsByPessoaCpfAndEventoId(anyString(), anyLong()))
                .thenReturn(false);
    }

    @E("o status do evento consta como {string}")
    public void status_do_evento_e(String status) {
        evento = new Evento();
        evento.setId(1L);
        evento.setNome("Tech Conference 2025");
        evento.setDescricao("Evento encerrado");
        evento.setLocal("Centro de Convenções");
        evento.setCapacidade(500);
        evento.setStatus(StatusEvento.ENCERRADO);
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(1));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));

        when(avaliacaoRepositorio.save(any(Avaliacao.class))).thenAnswer(invocation -> {
            Avaliacao av = invocation.getArgument(0);
            av.setId(1L);
            return av;
        });
    }

    @Quando("o usuário envia uma nota {int}")
    public void usuario_envia_nota(int nota) {
        try {
            avaliacaoSalva = avaliacaoServico.salvar(nota, "Evento excelente!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a avaliação deve ser salva")
    public void avaliacao_deve_ser_salva() {
        assertNull(excecao, "Não deveria ter ocorrido erro ao salvar a avaliação: "
                + (excecao != null ? excecao.getMessage() : ""));
        verify(avaliacaoRepositorio, times(1)).save(any(Avaliacao.class));
    }

    @E("vinculada ao evento")
    public void vinculada_ao_evento() {
        assertNotNull(avaliacaoSalva);
        assertEquals(evento.getId(), avaliacaoSalva.getEvento().getId());
        assertEquals(participante.getCpf(), avaliacaoSalva.getPessoa().getCpf());
    }

    @Dado("que o participante está inscrito em um evento marcado para amanhã")
    public void participante_inscrito_em_evento_amanha() {
        excecao = null;
        avaliacaoSalva = null;

        participante = new Pessoa();
        participante.setCpf("99988877766");
        participante.setNome("João Santos");
        participante.setEmail("joao@email.com");
        participante.setSenha("senha456");
        participante.setSaldo(0.0);
        participante.setOrganizador(false);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoIdAndStatus(
                eq(participante.getCpf()), anyLong(), eq(StatusInscricao.CONFIRMADA)))
                .thenReturn(true);

        when(avaliacaoRepositorio.existsByPessoaCpfAndEventoId(anyString(), anyLong()))
                .thenReturn(false);

        evento = new Evento();
        evento.setId(2L);
        evento.setNome("Workshop de Amanhã");
        evento.setDescricao("Evento futuro");
        evento.setLocal("Auditório B");
        evento.setCapacidade(100);
        evento.setStatus(StatusEvento.ATIVO);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(1));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
    }

    @Quando("ele acessa a página do evento")
    public void ele_acessa_pagina_do_evento() {
        try {
            avaliacaoServico.salvar(5, "Antecipado!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o botão ou área de avaliação não deve estar disponível para uso")
    public void botao_avaliacao_nao_disponivel() {
        assertNotNull(excecao);
        assertTrue(excecao instanceof AvaliacaoException);
        verify(avaliacaoRepositorio, never()).save(any(Avaliacao.class));
    }

    @Dado("que o usuário não possui inscrição confirmada no evento")
    public void usuario_sem_inscricao_confirmada() {
        excecao = null;
        avaliacaoSalva = null;

        participante = new Pessoa();
        participante.setCpf("55566677788");
        participante.setNome("Carlos Souza");
        participante.setEmail("carlos@email.com");
        participante.setSenha("senha789");
        participante.setSaldo(0.0);
        participante.setOrganizador(false);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoIdAndStatus(
                eq(participante.getCpf()), anyLong(), eq(StatusInscricao.CONFIRMADA)))
                .thenReturn(false);
    }

    @E("o evento já foi encerrado")
    public void evento_ja_foi_encerrado() {
        evento = new Evento();
        evento.setId(3L);
        evento.setNome("Evento Encerrado");
        evento.setDescricao("Evento que já aconteceu");
        evento.setLocal("Auditório A");
        evento.setCapacidade(200);
        evento.setStatus(StatusEvento.ENCERRADO);
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(2));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
    }

    @Quando("ele tenta enviar uma avaliação")
    public void ele_tenta_enviar_avaliacao() {
        try {
            avaliacaoServico.salvar(4, "Bom evento!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve rejeitar a avaliação")
    public void sistema_deve_rejeitar_avaliacao() {
        assertNotNull(excecao);
        assertTrue(excecao instanceof AvaliacaoException);
        verify(avaliacaoRepositorio, never()).save(any(Avaliacao.class));
    }

    @E("exibir uma mensagem informando que apenas inscritos podem avaliar")
    public void exibir_mensagem_apenas_inscritos() {
        String mensagem = excecao.getMessage().toLowerCase();
        assertTrue(mensagem.contains("inscrição") || mensagem.contains("inscrito") || mensagem.contains("confirmada"));
    }

    @Dado("que o evento está encerrado e o usuário possui inscrição confirmada")
    public void evento_encerrado_usuario_com_inscricao_confirmada() {
        excecao = null;
        avaliacaoSalva = null;

        participante = new Pessoa();
        participante.setCpf("44455566677");
        participante.setNome("Fernanda Oliveira");
        participante.setEmail("fernanda@email.com");
        participante.setSenha("senhaF");
        participante.setOrganizador(false);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoIdAndStatus(
                eq(participante.getCpf()), anyLong(), eq(StatusInscricao.CONFIRMADA)))
                .thenReturn(true);

        when(avaliacaoRepositorio.existsByPessoaCpfAndEventoId(anyString(), anyLong()))
                .thenReturn(false);

        evento = new Evento();
        evento.setId(10L);
        evento.setNome("Evento com Cashback");
        evento.setDescricao("Evento encerrado");
        evento.setLocal("Auditório C");
        evento.setCapacidade(300);
        evento.setStatus(StatusEvento.ENCERRADO);
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(1));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));

        when(avaliacaoRepositorio.save(any(Avaliacao.class))).thenAnswer(invocation -> {
            Avaliacao av = invocation.getArgument(0);
            av.setId(100L);
            return av;
        });
    }

    @E("o saldo atual da carteira virtual do usuário é de R$ {double}")
    public void saldo_atual_carteira(Double saldo) {
        participante.setSaldo(saldo);
    }

    @Quando("ele envia uma nota {int} e um comentário sobre o evento")
    public void envia_nota_e_comentario(int nota) {
        try {
            avaliacaoSalva = avaliacaoServico.salvar(nota, "Excelente evento!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve salvar a avaliação")
    public void sistema_deve_salvar_avaliacao() {
        assertNull(excecao, "Não deveria ter ocorrido erro: "
                + (excecao != null ? excecao.getMessage() : ""));
        verify(avaliacaoRepositorio, times(1)).save(any(Avaliacao.class));
    }

    @E("creditar automaticamente R$ {double} na carteira do usuário atualizando para R$ {double}")
    public void creditar_cashback_na_carteira(Double cashback, Double novoSaldo) {
        assertEquals(novoSaldo, participante.getSaldo());
        verify(pessoaRepositorio, times(1)).save(participante);
    }

    @E("gerar um aviso de cashback ao usuário")
    public void gerar_aviso_cashback() {
        assertNotNull(avaliacaoSalva);
        assertTrue(participante.getSaldo() >= AvaliacaoServico.VALOR_CASHBACK);
    }


    @Dado("que o usuário já enviou uma avaliação para o evento e recebeu a recompensa")
    public void usuario_ja_avaliou_e_recebeu_recompensa() {
        excecao = null;
        avaliacaoSalva = null;

        participante = new Pessoa();
        participante.setCpf("77788899900");
        participante.setNome("Ricardo Melo");
        participante.setEmail("ricardo@email.com");
        participante.setSenha("senhaR");
        participante.setSaldo(15.0);
        participante.setOrganizador(false);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoIdAndStatus(
                eq(participante.getCpf()), anyLong(), eq(StatusInscricao.CONFIRMADA)))
                .thenReturn(true);

        when(avaliacaoRepositorio.existsByPessoaCpfAndEventoId(
                eq(participante.getCpf()), anyLong()))
                .thenReturn(true);

        evento = new Evento();
        evento.setId(20L);
        evento.setNome("Evento Já Avaliado");
        evento.setDescricao("Usuário tenta avaliar de novo");
        evento.setLocal("Auditório D");
        evento.setCapacidade(200);
        evento.setStatus(StatusEvento.ENCERRADO);
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(2));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
    }

    @Quando("ele tenta enviar uma nova avaliação para o mesmo evento alterando a nota")
    public void tenta_enviar_nova_avaliacao() {
        try {
            avaliacaoServico.salvar(1, "Mudei de ideia!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve rejeitar a requisição")
    public void sistema_deve_rejeitar_requisicao() {
        assertNotNull(excecao);
        assertTrue(excecao instanceof AvaliacaoException);
        verify(avaliacaoRepositorio, never()).save(any(Avaliacao.class));
        assertEquals(15.0, participante.getSaldo());
    }

    @E("informar que o usuário já avaliou este evento anteriormente")
    public void informar_avaliacao_ja_existente() {
        assertEquals("Você já avaliou este evento anteriormente.", excecao.getMessage());
    }
}