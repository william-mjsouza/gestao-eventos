package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.entity.Avaliacao;
import com.gestaoeventos.entity.Evento;
import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.exception.AvaliacaoException;
import com.gestaoeventos.repository.AvaliacaoRepository;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.InscricaoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import com.gestaoeventos.service.AvaliacaoService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AvaliacaoSteps {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

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

        when(pessoaRepository.findById(participante.getCpf()))
                .thenReturn(Optional.of(participante));

        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(
                eq(participante.getCpf()), anyLong()))
                .thenReturn(true);
    }

    @E("o status do evento consta como {string}")
    public void status_do_evento_e(String status) {
        evento = new Evento();
        evento.setId(1L);
        evento.setNome("Tech Conference 2025");
        evento.setDescricao("Evento encerrado");
        evento.setLocal("Centro de Convenções");
        evento.setCapacidade(500);
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(1));

        when(eventoRepository.findById(evento.getId()))
                .thenReturn(Optional.of(evento));

        when(avaliacaoRepository.save(any(Avaliacao.class)))
                .thenAnswer(invocation -> {
                    Avaliacao av = invocation.getArgument(0);
                    av.setId(1L);
                    return av;
                });
    }

    @Quando("o usuário envia uma nota {int}")
    public void usuario_envia_nota(int nota) {
        try {
            avaliacaoSalva = avaliacaoService.salvar(nota, "Evento excelente!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a avaliação deve ser salva")
    public void avaliacao_deve_ser_salva() {
        assertNull(excecao, "Não deveria ter ocorrido erro ao salvar a avaliação: "
                + (excecao != null ? excecao.getMessage() : ""));
        verify(avaliacaoRepository, times(1)).save(any(Avaliacao.class));
    }

    @E("vinculada ao evento")
    public void vinculada_ao_evento() {
        assertNotNull(avaliacaoSalva, "A avaliação salva não deveria ser nula.");
        assertEquals(evento.getId(), avaliacaoSalva.getEvento().getId(),
                "A avaliação deveria estar vinculada ao evento correto.");
        assertEquals(participante.getCpf(), avaliacaoSalva.getPessoa().getCpf(),
                "A avaliação deveria estar vinculada ao participante correto.");
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

        when(pessoaRepository.findById(participante.getCpf()))
                .thenReturn(Optional.of(participante));

        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(
                eq(participante.getCpf()), anyLong()))
                .thenReturn(true);

        evento = new Evento();
        evento.setId(2L);
        evento.setNome("Workshop de Amanhã");
        evento.setDescricao("Evento futuro");
        evento.setLocal("Auditório B");
        evento.setCapacidade(100);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(1));

        when(eventoRepository.findById(evento.getId()))
                .thenReturn(Optional.of(evento));
    }

    @Quando("ele acessa a página do evento")
    public void ele_acessa_pagina_do_evento() {
        try {
            avaliacaoService.salvar(5, "Antecipado!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o botão ou área de avaliação não deve estar disponível para uso")
    public void botao_avaliacao_nao_disponivel() {
        assertNotNull(excecao, "O sistema deveria ter bloqueado a avaliação antecipada.");
        assertTrue(excecao instanceof AvaliacaoException,
                "A exceção deveria ser uma AvaliacaoException.");
        verify(avaliacaoRepository, never()).save(any(Avaliacao.class));
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

        when(pessoaRepository.findById(participante.getCpf()))
                .thenReturn(Optional.of(participante));

        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(
                eq(participante.getCpf()), anyLong()))
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
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(2));

        when(eventoRepository.findById(evento.getId()))
                .thenReturn(Optional.of(evento));
    }

    @Quando("ele tenta enviar uma avaliação")
    public void ele_tenta_enviar_avaliacao() {
        try {
            avaliacaoService.salvar(4, "Bom evento!", evento.getId(), participante.getCpf());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve rejeitar a avaliação")
    public void sistema_deve_rejeitar_avaliacao() {
        assertNotNull(excecao, "O sistema deveria ter rejeitado a avaliação.");
        assertTrue(excecao instanceof AvaliacaoException,
                "A exceção deveria ser uma AvaliacaoException.");
        verify(avaliacaoRepository, never()).save(any(Avaliacao.class));
    }

    @E("exibir uma mensagem informando que apenas inscritos podem avaliar")
    public void exibir_mensagem_apenas_inscritos() {
        String mensagem = excecao.getMessage().toLowerCase();
        assertTrue(mensagem.contains("inscrição") || mensagem.contains("inscrito") || mensagem.contains("confirmada"),
                "A mensagem deveria informar que apenas inscritos podem avaliar. Mensagem atual: "
                        + excecao.getMessage());
    }
}