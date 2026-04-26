package com.gestaoeventos.dominio.inscricao.cupom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CupomRepositorio extends JpaRepository<Cupom, String> {
}
