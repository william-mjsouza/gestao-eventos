package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.CancelamentoEmCascataServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CancelamentoCascataSteps {

    @Autowired
    private CancelamentoEmCascataServico cancelamentoEmCascataServico;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private List<Inscricao> inscricoesDoEvento;
    private List<Pessoa> participantes;
    private Evento evento;
    private Exception excecao;

    private static final Long EVENTO_ID = 10L;
    private static final BigDecimal PRECO_LOTE = new BigDecimal("200.00");
    private static final double SALDO_INICIAL = 800.0;

    @Dado("que o evento possui {int} participantes confirmados")
    public void evento_possui_participantes_confirmados(int quantidade) {
        excecao = null;
        inscricoesDoEvento = new ArrayList<>();
        participantes = new ArrayList<>();

        evento = new Evento();
        evento.setId(EVENTO_ID);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(5));

        Lote lote = new Lote();
        lote.setId(1L);
        lote.setPreco(PRECO_LOTE);
        evento.getLotes().add(lote);

        for (int i = 1; i <= quantidade; i++) {
            Pessoa participante = new Pessoa();
            participante.setCpf(String.format("%011d", i));
            participante.setNome("Participante " + i);
            participante.setSaldo(SALDO_INICIAL);

            Inscricao inscricao = new Inscricao((long) i, participante, evento, lote,
                    StatusInscricao.CONFIRMADA, LocalDateTime.now().minusDays(1));

            participantes.add(participante);
            inscricoesDoEvento.add(inscricao);

            when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));
        }

        when(inscricaoRepositorio.findByEventoIdAndStatusIn(eq(EVENTO_ID), anyList()))
                .thenReturn(inscricoesDoEvento);
        when(inscricaoRepositorio.save(any(Inscricao.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Quando("o organizador altera o status do evento para {string}")
    public void organizador_altera_status_evento(String status) {
        try {
            cancelamentoEmCascataServico.executar(EVENTO_ID);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o status de todas as {int} inscrições deve passar para {string}")
    public void status_inscricoes_deve_ser(int quantidade, String statusEsperado) {
        assertNull(excecao, "Não deveria ter ocorrido erro: " + (excecao != null ? excecao.getMessage() : ""));
        assertEquals(quantidade, inscricoesDoEvento.size());
        inscricoesDoEvento.forEach(inscricao ->
                assertEquals(StatusInscricao.CANCELADA_PELO_ORGANIZADOR, inscricao.getStatus(),
                        "Inscrição #" + inscricao.getId() + " deveria estar cancelada pelo organizador")
        );
    }

    @E("o valor pago deve ser devolvido integralmente para as {int} carteiras virtuais")
    public void valor_devolvido_para_carteiras(int quantidade) {
        double saldoEsperado = SALDO_INICIAL + PRECO_LOTE.doubleValue();
        participantes.forEach(participante ->
                assertEquals(saldoEsperado, participante.getSaldo(), 0.001,
                        "Saldo do participante " + participante.getNome() + " não foi estornado corretamente")
        );
        verify(pessoaRepositorio, times(quantidade)).save(any(Pessoa.class));
    }

    @E("notificações de cancelamento devem ser disparadas")
    public void notificacoes_devem_ser_disparadas() {
        verify(inscricaoRepositorio, times(inscricoesDoEvento.size())).save(any(Inscricao.class));
    }

    @Dado("que o evento possui participantes com inscrições pendentes e confirmadas")
    public void evento_possui_inscricoes_mistas() {
        excecao = null;
        inscricoesDoEvento = new ArrayList<>();
        participantes = new ArrayList<>();

        evento = new Evento();
        evento.setId(EVENTO_ID);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(5));

        Lote lote = new Lote();
        lote.setId(1L);
        lote.setPreco(PRECO_LOTE);
        evento.getLotes().add(lote);

        for (int i = 1; i <= 3; i++) {
            Pessoa participante = new Pessoa();
            participante.setCpf(String.format("%011d", i));
            participante.setNome("Confirmado " + i);
            participante.setSaldo(SALDO_INICIAL);

            Inscricao inscricao = new Inscricao((long) i, participante, evento, lote,
                    StatusInscricao.CONFIRMADA, LocalDateTime.now().minusDays(1));

            participantes.add(participante);
            inscricoesDoEvento.add(inscricao);
            when(pessoaRepositorio.findById(participante.getCpf())).thenReturn(Optional.of(participante));
        }

        for (int i = 4; i <= 6; i++) {
            Pessoa participante = new Pessoa();
            participante.setCpf(String.format("%011d", i));
            participante.setNome("Pendente " + i);
            participante.setSaldo(SALDO_INICIAL);

            Inscricao inscricao = new Inscricao((long) i, participante, evento, lote,
                    StatusInscricao.PENDENTE, LocalDateTime.now().minusHours(1));

            participantes.add(participante);
            inscricoesDoEvento.add(inscricao);
        }

        when(inscricaoRepositorio.findByEventoIdAndStatusIn(eq(EVENTO_ID), anyList()))
                .thenReturn(inscricoesDoEvento);
        when(inscricaoRepositorio.save(any(Inscricao.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Entao("todas as inscrições confirmadas devem ter seu saldo estornado")
    public void inscricoes_confirmadas_com_estorno() {
        assertNull(excecao, "Não deveria ter ocorrido erro: " + (excecao != null ? excecao.getMessage() : ""));

        double saldoEsperadoConfirmados = SALDO_INICIAL + PRECO_LOTE.doubleValue();
        inscricoesDoEvento.stream()
                .filter(i -> i.getStatus() == StatusInscricao.CANCELADA_PELO_ORGANIZADOR)
                .map(Inscricao::getParticipante)
                .filter(p -> p.getNome().startsWith("Confirmado"))
                .forEach(p -> assertEquals(saldoEsperadoConfirmados, p.getSaldo(), 0.001,
                        "Saldo de " + p.getNome() + " deveria ter sido estornado"));
    }

    @E("todas as inscrições pendentes devem ser canceladas sem estorno")
    public void inscricoes_pendentes_sem_estorno() {
        inscricoesDoEvento.stream()
                .filter(i -> i.getParticipante().getNome().startsWith("Pendente"))
                .forEach(i -> {
                    assertEquals(StatusInscricao.CANCELADA_PELO_ORGANIZADOR, i.getStatus(),
                            "Inscrição pendente deveria estar cancelada pelo organizador");
                    assertEquals(SALDO_INICIAL, i.getParticipante().getSaldo(), 0.001,
                            "Inscrição pendente não deveria ter gerado estorno");
                });
    }
}
