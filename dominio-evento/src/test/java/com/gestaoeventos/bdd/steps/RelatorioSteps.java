package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.relatorio.RelatorioOcupacaoReceita;
import com.gestaoeventos.dominio.evento.relatorio.RelatorioRepositorio;
import com.gestaoeventos.dominio.evento.relatorio.RelatorioServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RelatorioSteps {

    @Autowired
    private RelatorioServico relatorioServico;

    @Autowired
    private RelatorioRepositorio relatorioRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    private Evento evento;
    private RelatorioOcupacaoReceita relatorio;
    private Exception excecao;

    @Dado("que um evento teve 10 vendas no Lote 1 de R$ {double} e 10 vendas no Lote 2 de R$ {double}")
    public void evento_com_vendas_em_dois_lotes(Double precoLote1, Double precoLote2) {
        excecao = null;
        relatorio = null;

        evento = new Evento();
        evento.setId(1L);
        evento.setNome("Tech Conference 2026");
        evento.setDescricao("Evento de tecnologia");
        evento.setLocal("Centro de Convenções");
        evento.setCapacidade(30);
        evento.setDataHoraInicio(LocalDateTime.now().plusDays(10));

        when(eventoRepositorio.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(relatorioRepositorio.contarInscricoesConfirmadas(evento.getId())).thenReturn(18);
        when(relatorioRepositorio.calcularReceitaLiquida(evento.getId()))
                .thenReturn(new BigDecimal("1300.00"));
    }

    @E("ocorreram 2 cancelamentos do Lote 2")
    public void ocorreram_cancelamentos() {
        
    }

    @Quando("o organizador solicita o relatório financeiro")
    public void organizador_solicita_relatorio() {
        try {
            relatorio = relatorioServico.gerarRelatorio(evento.getId());
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve agregar os dados corretamente")
    public void sistema_agrega_dados() {
        assertNull(excecao, "Não deveria ter ocorrido erro: "
                + (excecao != null ? excecao.getMessage() : ""));
        assertNotNull(relatorio, "O relatório não deveria ser nulo.");
    }

    @E("exibir a receita total líquida de R$ {double} e ocupação de {int} vagas ativas")
public void exibir_receita_e_ocupacao(Double receita, Integer vagasAtivas) {
    assertEquals(0, BigDecimal.valueOf(receita).compareTo(relatorio.getReceitaTotal()),
            "A receita total líquida não confere.");
    assertEquals(vagasAtivas, relatorio.getVagasOcupadas(),
            "O número de vagas ativas não confere.");

    double taxaEsperada = (vagasAtivas * 100.0) / evento.getCapacidade();
    assertEquals(taxaEsperada, relatorio.getTaxaOcupacao(), 0.01,
            "A taxa de ocupação não confere.");
}
}