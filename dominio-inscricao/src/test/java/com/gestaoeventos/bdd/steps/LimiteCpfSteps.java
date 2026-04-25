package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LimiteCpfSteps {

    @Autowired
    private InscricaoServico inscricaoServico;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Evento evento;
    private Pessoa participante;
    private Lote lote;
    private Exception excecao;

    @Dado("que o evento permite máximo de {int} ingressos por CPF")
    public void dadoEventoPermiteMaximoIngressosPorCpf(int limite) {
        participante = new Pessoa();
        participante.setCpf("99988877766");
        
        lote = new Lote();
        lote.setId(1L);
        lote.setQuantidadeDisponivel(100);
        
        evento = new Evento();
        evento.setId(1L);
        evento.setLimiteIngressosPorCpf(limite);
        evento.setStatus(StatusEvento.ATIVO);
        evento.setCapacidade(500);
        evento.setLotes(Collections.singletonList(lote));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));
        
        // Simular que o evento ainda tem vagas
        when(inscricaoRepositorio.countByEventoIdAndStatusNot(evento.getId(), StatusInscricao.CANCELADA)).thenReturn(10L);
    }

    @E("o usuário tem {int} inscrição confirmada e {int} ingresso aguardando pagamento no carrinho")
    public void usuarioTemInscricoesEIngressos(int confirmada, int pendente) {
        long totalAtual = confirmada + pendente;
        
        when(inscricaoRepositorio.countByParticipanteCpfAndEventoIdAndStatusIn(
                eq(participante.getCpf()),
                eq(evento.getId()),
                eq(Arrays.asList(StatusInscricao.PENDENTE, StatusInscricao.CONFIRMADA))
        )).thenReturn(totalAtual);
    }

    @Quando("ele tenta adicionar um terceiro ingresso ao carrinho")
    public void tentaAdicionarTerceiroIngresso() {
        excecao = null;
        try {
            inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), lote.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve realizar a soma")
    public void sistemaRealizaSoma() {
        verify(inscricaoRepositorio, times(1)).countByParticipanteCpfAndEventoIdAndStatusIn(
            eq(participante.getCpf()),
            eq(evento.getId()),
            eq(Arrays.asList(StatusInscricao.PENDENTE, StatusInscricao.CONFIRMADA))
        );
    }

    @E("bloquear a ação informando limite atingido")
    public void bloqueiaAcaoInformandoLimite() {
        assertNotNull(excecao, "Uma exceção deveria ser lançada por estourar o limite");
        assertTrue(excecao instanceof InscricaoException);
        assertEquals("Limite de ingressos por usuário atingido para este evento.", excecao.getMessage());
    }
}
