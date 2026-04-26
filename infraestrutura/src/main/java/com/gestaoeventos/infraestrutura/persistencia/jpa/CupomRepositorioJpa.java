package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.cupom.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupomRepositorioJpa extends JpaRepository<Cupom, String> {
}
