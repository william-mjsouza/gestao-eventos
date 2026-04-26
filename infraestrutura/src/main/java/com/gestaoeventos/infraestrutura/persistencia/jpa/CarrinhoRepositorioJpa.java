package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.dominio.inscricao.carrinho.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepositorioJpa extends JpaRepository<Carrinho, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    Optional<Carrinho> findByParticipanteCpf(String cpf);
}
