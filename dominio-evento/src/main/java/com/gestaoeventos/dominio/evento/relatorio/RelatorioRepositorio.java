package com.gestaoeventos.dominio.evento.relatorio;

import java.math.BigDecimal;

public interface RelatorioRepositorio {
    int contarInscricoesConfirmadas(Long eventoId);
    BigDecimal calcularReceitaLiquida(Long eventoId);
}
