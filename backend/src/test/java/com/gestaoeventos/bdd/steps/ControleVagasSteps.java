package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.entity.*;
import com.gestaoeventos.exception.InscricaoException;
import com.gestaoeventos.repository.*;
import com.gestaoeventos.service.InscricaoService;
import com.gestaoeventos.service.ListaEsperaService;
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

    @Autowired private InscricaoService inscricaoService;
    @Autowired private ListaEsperaService listaEsperaService;

    @Autowired private InscricaoRepository inscricaoRepository;
    @Autowired private EventoRepository eventoRepository;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private ListaEsperaRepository listaEsperaRepository;

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

        when(eventoRepository.findById(10L)).thenReturn(Optional.of(evento));
        when(pessoaRepository.findById("12345678901")).thenReturn(Optional.of(participante));
        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(inscricaoRepository.countByEventoIdAndStatusNot(10L, StatusInscricao.CANCELADA)).thenReturn(0L);
    }

    @Quando("um participante realiza a inscrição para {int} vaga")
    public void participante_realiza_inscricao(int qtd) {
        Inscricao pendente = new Inscricao(1L, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        Inscricao confirmada = new Inscricao(1L, participante, evento, lote, StatusInscricao.CONFIRMADA, LocalDateTime.now());

        when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(pendente);
        when(inscricaoRepository.findById(1L)).thenReturn(Optional.of(pendente));

        try {
            Inscricao pend = inscricaoService.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
            when(inscricaoRepository.countByEventoIdAndStatusNot(10L, StatusInscricao.CANCELADA)).thenReturn(1L);
            when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(confirmada);
            inscricaoGerada = inscricaoService.confirmarPagamento(pend.getId());
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
        long inscritos = inscricaoRepository.countByEventoIdAndStatusNot(evento.getId(), StatusInscricao.CANCELADA);
        int vagasRestantes = evento.getCapacidade() - (int) inscritos;
        assertEquals(vagasEsperadas, vagasRestantes,
                "O saldo de vagas deveria ser " + vagasEsperadas + " mas foi " + vagasRestantes);
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

        when(eventoRepository.findById(20L)).thenReturn(Optional.of(evento));
        when(pessoaRepository.findById("99988877766")).thenReturn(Optional.of(participante));
        when(inscricaoRepository.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(inscricaoRepository.countByEventoIdAndStatusNot(20L, StatusInscricao.CANCELADA)).thenReturn((long) capacidade);
        when(listaEsperaRepository.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(listaEsperaRepository.countByEventoIdAndStatus(20L, StatusListaEspera.AGUARDANDO)).thenReturn(0L);
    }

    @Quando("um usuário tenta se inscrever")
    public void usuario_tenta_se_inscrever() {
        try {
            inscricaoService.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        } catch (InscricaoException e) {
            excecao = e;
        }

        ListaEspera entrada = new ListaEspera();
        entrada.setId(1L);
        entrada.setParticipante(participante);
        entrada.setEvento(evento);
        entrada.setPosicao(1);
        entrada.setStatus(StatusListaEspera.AGUARDANDO);

        when(listaEsperaRepository.save(any(ListaEspera.class))).thenReturn(entrada);

        try {
            entradaEspera = listaEsperaService.entrarNaFila(participante.getCpf(), evento.getId());
        } catch (Exception e2) {
            if (excecao == null) excecao = e2;
        }
    }

    @Entao("o sistema deve incluir na lista de espera")
    public void sistema_inclui_na_lista_espera() {
        assertNotNull(entradaEspera, "O participante deveria ter sido incluído na lista de espera.");
        assertEquals(StatusListaEspera.AGUARDANDO, entradaEspera.getStatus(),
                "O status na lista de espera deveria ser AGUARDANDO.");
        assertEquals(1, entradaEspera.getPosicao(), "A posição na fila deveria ser 1.");
    }
}
