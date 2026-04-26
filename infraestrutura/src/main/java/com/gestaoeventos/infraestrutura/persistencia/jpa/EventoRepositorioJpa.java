package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.evento.evento.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventoRepositorioJpa extends JpaRepository<Evento, Long> {
    boolean existsByNome(String nome);

    @Query("SELECT COUNT(e) > 0 FROM Evento e WHERE e.local = :local " +
           "AND e.status != 'CANCELADO' " +
           "AND e.dataHoraInicio < :termino AND e.dataHoraTermino > :inicio")
    boolean existeColisaoLocalEHorario(@Param("local") String local,
                                       @Param("inicio") LocalDateTime inicio,
                                       @Param("termino") LocalDateTime termino);
}
