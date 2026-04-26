package com.gestaoeventos.dominio.evento.lote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoteRepositorio extends JpaRepository<Lote, Long> {
}