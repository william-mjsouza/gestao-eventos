package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.*;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.*;
import com.gestaoeventos.dominio.participante.pessoa.*;
import com.gestaoeventos.dominio.compartilhado.*;
import io.cucumber.java.pt.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class controleHorarioSteps {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private InscricaoServico inscricaoServico;

    // Repositórios mockados globalmente — vamos configurar o comportamento deles
    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    private String mensagemErro;
    private String dataCenario;

    private final String cpfParticipante = "52998224725";
    private final String cpfOrganizador  = "11144477735";

    private static final LocalDateTime VENDA_INICIO = LocalDateTime.now().minusDays(1);
    private static final LocalDateTime VENDA_FIM    = LocalDateTime.of(2026, 10, 10, 23, 59);

    @Dado("que o participante está inscrito em um evento das {string} às {string} no dia {string}")
    public void dado(String inicio, String fim, String data) {
        this.dataCenario = data;

        // 1. Monta os objetos em memória
        Pessoa participante = new Pessoa();
        participante.setCpf(cpfParticipante);
        participante.setNome("Thiago");
        participante.setEmail("thiago@test.com");
        participante.setSenha("SenhaSegura123");
        participante.setSaldo(100.0);

        Pessoa org = new Pessoa();
        org.setCpf(cpfOrganizador);
        org.setNome("Organizador Teste");
        org.setEmail("org@test.com");
        org.setSenha("SenhaSegura123");

        Evento eventoExistente = new Evento();
        eventoExistente.setNome("Evento Ja Inscrito");
        eventoExistente.setDescricao("Evento de teste");
        eventoExistente.setLocal("Local Teste");
        eventoExistente.setOrganizador(org);
        eventoExistente.setStatus(StatusEvento.ATIVO);
        eventoExistente.setDataHoraInicio(LocalDateTime.parse(data + "T" + inicio + ":00"));
        eventoExistente.setDataHoraFim(LocalDateTime.parse(data + "T" + fim + ":00"));
        eventoExistente.setCapacidade(100);
        eventoExistente.setLotes(new ArrayList<>());

        Lote lote = new Lote();
        lote.setNome("Lote Unico");
        lote.setPreco(BigDecimal.ZERO);
        lote.setQuantidadeTotal(100);
        lote.setQuantidadeDisponivel(100);
        lote.setDataInicioVenda(VENDA_INICIO);
        lote.setDataFimVenda(VENDA_FIM);
        eventoExistente.getLotes().add(lote);

        Inscricao inscricaoExistente = new Inscricao();
        inscricaoExistente.setParticipante(participante);
        inscricaoExistente.setEvento(eventoExistente);
        inscricaoExistente.setLote(lote);
        inscricaoExistente.setStatus(StatusInscricao.CONFIRMADA);

        // 2. Configura os mocks para responder com os objetos reais
        Mockito.when(pessoaRepositorio.findById(cpfParticipante))
                .thenReturn(Optional.of(participante));

        Mockito.when(pessoaRepositorio.findById(cpfOrganizador))
                .thenReturn(Optional.of(org));

        // buscarConflitos: retorna a inscrição existente quando há sobreposição
        // A query verifica: inicio_existente < fim_novo AND fim_existente > inicio_novo
        // Configuramos para qualquer chamada retornar o conflito (validação feita pelo serviço)
        Mockito.when(inscricaoRepositorio.buscarConflitos(
                        Mockito.eq(cpfParticipante),
                        Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(inscricaoExistente));

        // existsByParticipanteCpfAndEventoId: false para novo evento (ainda não inscrito)
        Mockito.when(inscricaoRepositorio.existsByParticipanteCpfAndEventoId(
                        Mockito.eq(cpfParticipante), Mockito.anyLong()))
                .thenReturn(false);
    }

    @Quando("ele tenta se inscrever em um novo evento das {string} às {string} no mesmo dia")
    public void quando(String inicio, String fim) {
        try {
            Pessoa org = pessoaRepositorio.findById(cpfOrganizador).get();

            // Cria o novo evento conflitante com ID fake para o serviço encontrar
            Evento novoEvento = new Evento();
            novoEvento.setNome("Novo Evento Conflitante");
            novoEvento.setDescricao("Evento conflitante para teste");
            novoEvento.setLocal("Local Conflito");
            novoEvento.setOrganizador(org);
            novoEvento.setStatus(StatusEvento.ATIVO);
            novoEvento.setDataHoraInicio(LocalDateTime.parse(dataCenario + "T" + inicio + ":00"));
            novoEvento.setDataHoraFim(LocalDateTime.parse(dataCenario + "T" + fim + ":00"));
            novoEvento.setCapacidade(100);

            Lote novoLote = new Lote();
            novoLote.setNome("Lote Conflito");
            novoLote.setPreco(BigDecimal.ZERO);
            novoLote.setQuantidadeTotal(100);
            novoLote.setQuantidadeDisponivel(100);
            novoLote.setDataInicioVenda(VENDA_INICIO);
            novoLote.setDataFimVenda(VENDA_FIM);
            novoEvento.setLotes(List.of(novoLote));

            Long eventoId = 999L;
            Long loteId   = 999L;

            // Configura mock para o serviço encontrar o novo evento e o participante
            Mockito.when(eventoRepositorio.findById(eventoId))
                    .thenReturn(Optional.of(novoEvento));

            Mockito.when(inscricaoRepositorio.countByEventoIdAndStatusNot(
                            Mockito.eq(eventoId), Mockito.any()))
                    .thenReturn(0L);

            // Chama o serviço — deve lançar exceção de conflito
            inscricaoServico.iniciarInscricao(cpfParticipante, eventoId, loteId);

        } catch (Exception e) {
            mensagemErro = e.getMessage();
        }
    }

    @Então("o sistema deve bloquear a inscrição por choque de agenda")
    public void entao() {
        Assertions.assertNotNull(mensagemErro,
                "O sistema deveria ter lançado uma exceção de conflito.");
        Assertions.assertTrue(mensagemErro.toLowerCase().contains("conflito"),
                "A mensagem esperada não foi encontrada. Recebido: " + mensagemErro);
    }
}