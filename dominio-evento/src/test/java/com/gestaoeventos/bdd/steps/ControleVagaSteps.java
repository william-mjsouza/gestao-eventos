package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoException;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.evento.EventoServico;
import com.gestaoeventos.dominio.evento.lote.Lote;
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
import static org.mockito.Mockito.*;

public class ControleVagaSteps {

    private Pessoa participante;
    private String resultadoInscricao;
    @Autowired
    private EventoServico eventoServico;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Evento evento;
    private Pessoa organizador;
    private Exception excecao;

    @Dado("que o evento possui {int} vagas disponíveis")
    public void evento_possui_vagas(int vagas) {

        organizador = new Pessoa();
        organizador.setCpf("123");
        organizador.setOrganizador(true);

        participante = new Pessoa();
        participante.setCpf("999");

        evento = new Evento();
        evento.setOrganizador(organizador);
        evento.setCapacidade(vagas);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(10));

        Lote lote = new Lote();
        lote.setNome("Lote 1");
        lote.setPreco(new BigDecimal("100"));
        lote.setQuantidadeTotal(vagas);
        lote.setQuantidadeDisponivel(vagas);
        lote.setDataInicioVenda(LocalDateTime.now().minusDays(1));
        lote.setDataFimVenda(LocalDateTime.now().plusDays(5));

        evento.getLotes().add(lote);

        when(eventoRepositorio.findById(any())).thenReturn(Optional.of(evento));
        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));
    }

    @Quando("um participante realiza a inscrição")
    public void participante_realiza_inscricao() {
        resultadoInscricao = eventoServico.inscrever(1L, participante.getCpf());
    }

    @Entao("a inscrição deve ser confirmada")
    public void inscricao_confirmada() {
        assertEquals("Inscrição confirmada", resultadoInscricao);
    }

    @E("o saldo de vagas atualizado para {int}")
    public void saldo_vagas_atualizado(int esperado) {
        int vagas = evento.getTotalVagasDisponiveis();
        assertEquals(esperado, vagas);
    }

    @Dado("que o evento atingiu seu limite máximo de vagas")
    public void evento_lotado() {

        organizador = new Pessoa();
        organizador.setCpf("123");
        organizador.setOrganizador(true);

        participante = new Pessoa();
        participante.setCpf("999");

        evento = new Evento();
        evento.setOrganizador(organizador);
        evento.setCapacidade(1);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(10));

        Lote lote = new Lote();
        lote.setNome("Lote 1");
        lote.setPreco(new BigDecimal("100"));
        lote.setQuantidadeTotal(1);
        lote.setQuantidadeDisponivel(0); // 🔥 já lotado
        lote.setDataInicioVenda(LocalDateTime.now().minusDays(1));
        lote.setDataFimVenda(LocalDateTime.now().plusDays(5));

        evento.getLotes().add(lote);

        when(eventoRepositorio.findById(any())).thenReturn(Optional.of(evento));
        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));
    }

    @Entao("o sistema deve incluir na lista de espera")
    public void deve_ir_para_lista_espera() {
        assertEquals("Adicionado à lista de espera", resultadoInscricao);
    }
}
