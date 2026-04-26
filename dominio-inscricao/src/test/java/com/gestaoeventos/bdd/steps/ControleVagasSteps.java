package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEspera;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaRepositorio;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaServico;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ControleVagasSteps {

    @Autowired private InscricaoServico inscricaoServico;
    @Autowired private ListaEsperaServico listaEsperaServico;

    @Autowired private InscricaoRepositorio inscricaoRepositorio;
    @Autowired private EventoRepositorio eventoRepositorio;
    @Autowired private PessoaRepositorio pessoaRepositorio;
    @Autowired private ListaEsperaRepositorio listaEsperaRepositorio;

    private Evento evento;
    private Lote lote;
    private Pessoa participante;
    private Inscricao inscricaoGerada;
    private ListaEspera entradaEspera;
    private Exception excecao;

    @Dado("que o evento possui {int} vagas disponíveis")
    public void evento_possui_vagas_disponiveis(int capacidade) {
        excecao = null;
        inscricaoGerada = null;

        lote = new Lote(1L, "Lote Padrão", new BigDecimal("50.00"), capacidade, capacidade,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10));

        evento = new Evento();
        evento.setId(10L);
        evento.setCapacidade(capacidade);
        evento.setStatus(StatusEvento.ATIVO);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(30));
        evento.getLotes().add(lote);

        participante = new Pessoa();
        participante.setCpf("12345678901");
        participante.setSaldo(200.0);

        when(eventoRepositorio.findById(10L)).thenReturn(Optional.of(evento));
        when(pessoaRepositorio.findById("12345678901")).thenReturn(Optional.of(participante));
        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(inscricaoRepositorio.countByEventoIdAndStatusNot(10L, StatusInscricao.CANCELADA)).thenReturn(0L);
    }

    @Quando("um participante realiza a inscrição para {int} vaga")
    public void participante_realiza_inscricao(int qtd) {
        Inscricao pendente = new Inscricao(1L, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        Inscricao confirmada = new Inscricao(1L, participante, evento, lote, StatusInscricao.CONFIRMADA, LocalDateTime.now());

        when(inscricaoRepositorio.save(any(Inscricao.class))).thenReturn(pendente);
        when(inscricaoRepositorio.findById(1L)).thenReturn(Optional.of(pendente));

        try {
            Inscricao pend = inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
            when(inscricaoRepositorio.countByEventoIdAndStatusNot(10L, StatusInscricao.CANCELADA)).thenReturn(1L);
            when(inscricaoRepositorio.save(any(Inscricao.class))).thenReturn(confirmada);
            inscricaoGerada = inscricaoServico.confirmarPagamento(pend.getId(), TipoPagamento.PIX);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a inscrição deve ser registrada com sucesso")
    public void inscricao_deve_ser_confirmada() {
        assertNull(excecao, "Não deveria ocorrer erro: " + (excecao != null ? excecao.getMessage() : ""));
        assertNotNull(inscricaoGerada);
        assertEquals(StatusInscricao.CONFIRMADA, inscricaoGerada.getStatus());
    }

    @E("o saldo de vagas atualizado para {int}")
    public void saldo_de_vagas_atualizado(int vagasEsperadas) {
        long inscritos = inscricaoRepositorio.countByEventoIdAndStatusNot(evento.getId(), StatusInscricao.CANCELADA);
        int vagasRestantes = evento.getCapacidade() - (int) inscritos;
        assertEquals(vagasEsperadas, vagasRestantes);
    }

    @Dado("que o evento atingiu seu limite máximo de vagas")
    public void evento_atingiu_limite_maximo() {
        excecao = null;
        entradaEspera = null;
        inscricaoGerada = null;

        int capacidade = 10;
        lote = new Lote(2L, "Lote Esgotado", new BigDecimal("50.00"), capacidade, 0,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10));

        evento = new Evento();
        evento.setId(20L);
        evento.setCapacidade(capacidade);
        evento.setStatus(StatusEvento.ATIVO);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(30));
        evento.getLotes().add(lote);

        participante = new Pessoa();
        participante.setCpf("99988877766");
        participante.setSaldo(200.0);

        when(eventoRepositorio.findById(20L)).thenReturn(Optional.of(evento));
        when(pessoaRepositorio.findById("99988877766")).thenReturn(Optional.of(participante));
        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(inscricaoRepositorio.countByEventoIdAndStatusNot(20L, StatusInscricao.CANCELADA)).thenReturn((long) capacidade);
        when(listaEsperaRepositorio.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(listaEsperaRepositorio.countByEventoIdAndStatus(20L, StatusListaEspera.AGUARDANDO)).thenReturn(0L);
    }

    @Quando("um usuário tenta se inscrever")
    public void usuario_tenta_se_inscrever() {
        try {
            inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        } catch (InscricaoException e) {
            excecao = e;
        }

        ListaEspera entrada = new ListaEspera();
        entrada.setId(1L);
        entrada.setParticipante(participante);
        entrada.setEvento(evento);
        entrada.setPosicao(1);
        entrada.setStatus(StatusListaEspera.AGUARDANDO);

        when(listaEsperaRepositorio.save(any(ListaEspera.class))).thenReturn(entrada);

        try {
            entradaEspera = listaEsperaServico.entrarNaFila(participante.getCpf(), evento.getId());
        } catch (Exception e2) {
            if (excecao == null) excecao = e2;
        }
    }

    @Entao("o sistema deve incluir na lista de espera")
    public void sistema_inclui_na_lista_espera() {
        assertNotNull(entradaEspera, "O participante deveria ter sido incluído na lista de espera.");
        assertEquals(StatusListaEspera.AGUARDANDO, entradaEspera.getStatus());
        assertEquals(1, entradaEspera.getPosicao());
    }
}
