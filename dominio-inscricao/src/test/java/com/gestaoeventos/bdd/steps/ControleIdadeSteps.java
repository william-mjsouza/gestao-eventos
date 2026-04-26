package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ControleIdadeSteps {

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private InscricaoServico inscricaoServico;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    private Evento evento;
    private Pessoa participante;
    private Exception excecao;
    private Inscricao inscricaoGerada;

    @Before
    public void setUp(){
        excecao = null;
        inscricaoGerada = null;

        // participante
        participante = new Pessoa();
        participante.setNome("Teste idade");
        participante.setSenha("123456");
        participante.setEmail("Teste@gmail.com");
        participante.setCpf("58453709068");
        participante.setOrganizador(false);
        participante.setSaldo(250.0);

        // evento
        evento = new Evento();
        evento.setId(50L);
        evento.setNome("Festa Controle de Idade");
        evento.setCapacidade(100);
        evento.setStatus(StatusEvento.ATIVO);

        Lote lote = new Lote(1L, "Lote Único", new BigDecimal("50.00"), 100, 100,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(30));
        evento.getLotes().add(lote);
    }

    @Dado("que o evento exige idade mínima de {int} anos")
    public void evento_idade_minima(int idadeMinima){
        evento.setIdadeMinima(idadeMinima);
        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
    }

    @E("a data de início do evento está marcada para {string}")
    public void data_inicio_evento_fixa(String dataString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataEvento = LocalDate.parse(dataString, formatter);
        evento.setDataHoraInicio(LocalDateTime.of(dataEvento, LocalTime.of(20, 0)));
    }

    @Quando("ele tenta realizar a inscrição")
    public void tenta_realizar_inscriao(){
        try{
            inscricaoGerada = inscricaoServico.iniciarInscricao(participante.getCpf(), evento.getId(), 1L);
        }
        catch (Exception e){
            excecao = e;
        }
    }

    @Entao("o sistema deve permitir a inscrição com sucesso")
    public void permitir_innscricao(){
        assertNull(excecao, "Não deveria ter lançado exceção!");
        assertNotNull(inscricaoGerada, "A inscrição deveria ter sido gerada.");
        assertEquals(StatusInscricao.PENDENTE, inscricaoGerada.getStatus());
    }

    @E("o participante informou sua data de nascimento como {string}")
    public void nascimento_data_exata(String dataNascimentoString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(dataNascimentoString, formatter);

        participante.setDataNascimento(data);

        when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));
        when(inscricaoRepositorio.existsByParticipanteCpfAndEventoId(anyString(), anyLong())).thenReturn(false);
        when(inscricaoRepositorio.countByEventoIdAndStatusNot(anyLong(), any())).thenReturn(0L);

        when(inscricaoRepositorio.save(any(Inscricao.class))).thenAnswer(i -> {
            Inscricao salva = i.getArgument(0);
            salva.setId(1L);
            return salva;
        });
    }

    @Entao("o sistema deve bloquear a inscrição")
    public void bloquear_inscricao() {
        assertNotNull(excecao, "O sistema deveria ter lançado uma exceção bloqueando a inscrição.");
        assertTrue(excecao instanceof InscricaoException, "O erro deve ser um InscricaoException");
        assertNull(inscricaoGerada, "A inscrição não deveria ter sido gerada.");
    }

    @E("exibir uma mensagem de erro alertando sobre a idade insuficiente")
    public void exibir_mensagem_erro_idade() {
        String mensagem = excecao.getMessage().toLowerCase();
        assertTrue(mensagem.contains("inferior") || mensagem.contains("idade mínima"),
                "A mensagem atual foi: " + excecao.getMessage());
    }
}