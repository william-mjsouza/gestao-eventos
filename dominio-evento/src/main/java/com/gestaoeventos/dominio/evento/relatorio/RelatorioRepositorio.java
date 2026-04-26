package com.gestaoeventos.dominio.evento.relatorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

@org.springframework.stereotype.Repository
public interface RelatorioRepositorio extends Repository<com.gestaoeventos.dominio.evento.evento.Evento, Long> {

    @Query(value = """
            SELECT COUNT(i.id)
            FROM INSCRICAO i
            WHERE i.evento_id = :eventoId
              AND i.status = 'CONFIRMADA'
            """, nativeQuery = true)
    int contarInscricoesConfirmadas(@Param("eventoId") Long eventoId);

    @Query(value = """
            SELECT COALESCE(SUM(l.preco), 0)
            FROM INSCRICAO i
            JOIN LOTE l ON i.lote_id = l.id
            WHERE i.evento_id = :eventoId
              AND i.status = 'CONFIRMADA'
            """, nativeQuery = true)
    BigDecimal calcularReceitaLiquida(@Param("eventoId") Long eventoId);
}