package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.evento.relatorio.RelatorioRepositorio;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class RelatorioRepositorioMemoria implements RelatorioRepositorio {

    @Override
    public int contarInscricoesConfirmadas(Long eventoId) {
        return 0;
    }

    @Override
    public BigDecimal calcularReceitaLiquida(Long eventoId) {
        return BigDecimal.ZERO;
    }
}
