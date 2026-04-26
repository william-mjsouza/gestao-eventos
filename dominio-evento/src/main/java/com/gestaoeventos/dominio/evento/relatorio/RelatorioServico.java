package com.gestaoeventos.dominio.evento.relatorio;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RelatorioServico {

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private RelatorioRepositorio relatorioRepositorio;

    public RelatorioOcupacaoReceita gerarRelatorio(Long eventoId) {
        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new RelatorioException("Evento não encontrado."));

        int vagasOcupadas = relatorioRepositorio.contarInscricoesConfirmadas(eventoId);
        BigDecimal receitaTotal = relatorioRepositorio.calcularReceitaLiquida(eventoId);

        if (receitaTotal == null) {
            receitaTotal = BigDecimal.ZERO;
        }

        return new RelatorioOcupacaoReceita(
                evento.getId(),
                evento.getNome(),
                evento.getCapacidade(),
                vagasOcupadas,
                receitaTotal);
    }
}