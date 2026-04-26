package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.evento.EventoServico;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class StatusEventoSteps {

    @Autowired
    private EventoServico eventoServico;

    @Autowired
    private InscricaoServico inscricaoServico;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Evento evento;
    private Pessoa organizador;
    private Pessoa participante;
    private Exception excecao;

    @Dado("que o evento possui todos os dados válidos")
    public void evento_possui_dados_validos() {
        excecao = null;

        organizador = new Pessoa();
        organizador.setCpf("12345678901");
        organizador.setNome("Organizador Teste");
        organizador.setEmail("org@email.com");
        organizador.setSenha("senha123");
        organizador.setSaldo(0.0);
        organizador.setOrganizador(true);

        when(pessoaRepositorio.findById(organizador.getCpf())).thenReturn(Optional.of(organizador));

        Lote lote = new Lote();
        lote.setNome("Lote Padrão");
        lote.setPreco(new BigDecimal("100.00"));
        lote.setQuantidadeTotal(100);
        lote.setQuantidadeDisponivel(100);
        lote.setDataInicioVenda(LocalDateTime.now());
        lote.setDataFimVenda(LocalDateTime.now().plusDays(10));

        evento = new Evento();
        evento.setId(1L);
        evento.setNome("Tech Conference 2026");
        evento.setDescricao("Evento de tecnologia");
        evento.setLocal("Centro de Convenções");
        evento.setCapacidade(500);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(30));
        evento.setOrganizador(organizador);
        evento.setStatus(StatusEvento.ATIVO);
        evento.getLotes().add(lote);

        when(eventoRepositorio.existsByNome(evento.getNome())).thenReturn(false);
        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(eventoRepositorio.save(any(Evento.class))).thenAnswer(i -> i.getArgument(0));
        //when(eventoServico.alterarStatus(eq(evento.getId()), any(StatusEvento.class)))
         //       .thenAnswer(invocation -> {
        //            StatusEvento novoStatus = invocation.getArgument(1);
         //           evento.setStatus(novoStatus);
        //            eventoRepositorio.save(evento);
        //            return evento;
        //        });
    }

    @Quando("o organizador publica o evento")
    public void organizador_publica_evento() {
        try {
            eventoServico.alterarStatus(evento.getId(), StatusEvento.ATIVO);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o evento deve ser publicado")
    public void evento_deve_ser_publicado() {
        assertNull(excecao, "Não deveria ter ocorrido erro ao publicar o evento: "
                + (excecao != null ? excecao.getMessage() : ""));
        verify(eventoRepositorio, times(1)).save(any(Evento.class));
    }

    @E("ficar disponível para inscrições")
    public void ficar_disponivel_para_inscricoes() {
        assertEquals(StatusEvento.ATIVO, evento.getStatus());
    }

    @Dado("que o status do evento consta como cancelado")
    public void status_evento_cancelado() {
        excecao = null;

        participante = new Pessoa();
        participante.setCpf("98765432100");
        participante.setNome("Participante Teste");
        participante.setEmail("part@email.com");
        participante.setSenha("senha456");
        participante.setSaldo(500.0);
        participante.setOrganizador(false);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));

        Lote lote = new Lote();
        lote.setId(1L);
        lote.setNome("Lote Padrão");
        lote.setPreco(new BigDecimal("100.00"));
        lote.setQuantidadeTotal(100);
        lote.setQuantidadeDisponivel(50);
        lote.setDataInicioVenda(LocalDateTime.now().minusDays(1));
        lote.setDataFimVenda(LocalDateTime.now().plusDays(10));

        evento = new Evento();
        evento.setId(2L);
        evento.setNome("Evento Cancelado");
        evento.setDescricao("Este evento foi cancelado");
        evento.setLocal("Local X");
        evento.setCapacidade(100);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(5));
        evento.setStatus(StatusEvento.CANCELADO);
        evento.getLotes().add(lote);

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoId(participante.getCpf(), evento.getId()))
                .thenReturn(false);
        when(inscricaoRepositorio.countByEventoId(evento.getId())).thenReturn(10L);
    }

    @Quando("um usuário tenta se inscrever ao evento")
    public void usuario_tenta_se_inscrever_ao_evento() {
        try {
            inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), 1L);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve impedir a ação")
    public void sistema_deve_impedir_acao() {
        assertNotNull(excecao);
        assertTrue(excecao instanceof InscricaoException);
        verify(inscricaoRepositorio, never()).save(any());
    }

    @E("informar que o evento foi cancelado")
    public void informar_evento_cancelado() {
        String mensagem = excecao.getMessage().toLowerCase();
        assertTrue(mensagem.contains("cancelado"));
    }
}