package com.gestaoeventos.dominio.inscricao.carrinho;

import java.util.Optional;

public interface CarrinhoRepositorio {
    Optional<Carrinho> findById(Long id);
    Carrinho save(Carrinho carrinho);
    void delete(Carrinho carrinho);
    boolean existsByParticipanteCpfAndEventoId(String cpf, Long eventoId);
    Optional<Carrinho> findByParticipanteCpf(String cpf);
}
