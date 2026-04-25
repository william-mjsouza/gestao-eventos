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

public class EventoSteps {

    @Autowired
    private EventoServico eventoServico;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Evento evento;
    private Pessoa organizador;
    private Exception excecao;

    @Dado("que o usuário é um organizador válido")
    @Dado("que o usuário é um organizador")
    public void usuario_e_organizador_valido() {
        organizador = new Pessoa();
        organizador.setCpf("12345678901");
        organizador.setOrganizador(true);

        excecao = null;

        when(pessoaRepositorio.findById(organizador.getCpf())).thenReturn(Optional.of(organizador));

        evento = new Evento();
        evento.setOrganizador(organizador);
    }

    @E("preenche todos os campos obrigatórios corretamente com data futura")
    public void preenche_campos_com_data_futura() {
        evento.setNome("Tech Conference 2026");
        evento.setDescricao("Maior evento de tecnologia");
        evento.setLocal("Centro de Convenções");
        evento.setCapacidade(500);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(30));
        evento.setIdadeMinima(18);

        when(eventoRepositorio.existsByNome(evento.getNome())).thenReturn(false);
    }

    @E("adiciona pelo menos um lote válido")
    public void adiciona_lote_valido() {
        Lote lote = new Lote();
        lote.setNome("Lote VIP");
        lote.setPreco(new BigDecimal("150.00"));
        lote.setQuantidadeTotal(100);
        lote.setQuantidadeDisponivel(100);
        lote.setDataInicioVenda(LocalDateTime.now());
        lote.setDataFimVenda(LocalDateTime.now().plusDays(29));

        evento.getLotes().add(lote);
    }

    @Quando("ele solicita a criação do evento")
    public void solicita_criacao_do_evento() {
        try {
            eventoServico.salvar(evento);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o evento deve ser criado e postado")
    public void evento_deve_ser_criado() {
        assertNull(excecao, "Não deveria ter ocorrido erro na criação de um evento válido");
        verify(eventoRepositorio, times(1)).save(any(Evento.class));
    }

    @Quando("ele tenta criar um evento com a data de início anterior ao dia de hoje")
    public void tenta_criar_evento_com_data_passada() {
        evento.setNome("Evento Atrasado");
        evento.setDescricao("Um evento que já passou");
        evento.setLocal("Teatro Municipal");
        evento.setCapacidade(200);
        evento.setDataHoraInicio(LocalDateTime.now().minusDays(5));
        evento.setIdadeMinima(18);

        adiciona_lote_valido();

        try {
            eventoServico.salvar(evento);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve rejeitar a criação")
    public void sistema_deve_rejeitar_criacao() {
        assertNotNull(excecao, "Uma exceção deveria ter sido lançada");
        verify(eventoRepositorio, never()).save(any(Evento.class));
    }

    @E("exibir uma mensagem de erro de data inválida")
    public void exibir_mensagem_erro_data() {
        assertTrue(excecao instanceof EventoException, "A exceção lançada deveria ser uma EventoException");
        String mensagemErro = excecao.getMessage().toLowerCase();
        assertTrue(mensagemErro.contains("data"), "A mensagem de erro deveria mencionar o problema com a data");
    }
}
