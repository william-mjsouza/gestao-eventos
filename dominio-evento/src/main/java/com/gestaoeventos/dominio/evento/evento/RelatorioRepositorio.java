package com.gestaoeventos.dominio.evento.relatorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;

import java.math.BigDecimal;

@Repository
public interface RelatorioRepositorio extends JpaRepository<Inscricao, Long> {

    @Query("""
            SELECT COUNT(i)
            FROM Inscricao i
            WHERE i.evento.id = :eventoId
              AND i.status = com.gestaoeventos.dominio.compartilhado.StatusInscricao.CONFIRMADA
            """)
    int contarInscricoesConfirmadas(@Param("eventoId") Long eventoId);

    @Query("""
            SELECT COALESCE(SUM(i.lote.preco), 0)
            FROM Inscricao i
            WHERE i.evento.id = :eventoId
              AND i.status = com.gestaoeventos.dominio.compartilhado.StatusInscricao.CONFIRMADA
            """)
    BigDecimal calcularReceitaLiquida(@Param("eventoId") Long eventoId);
}