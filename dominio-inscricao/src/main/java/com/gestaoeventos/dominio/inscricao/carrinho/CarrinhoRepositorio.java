package com.gestaoeventos.dominio.inscricao.carrinho;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrinhoRepositorio extends JpaRepository<Carrinho, Long> {
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    Optional<Carrinho> findByParticipanteCpf(String cpf);
}