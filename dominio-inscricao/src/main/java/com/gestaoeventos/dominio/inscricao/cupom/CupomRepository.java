package com.gestaoeventos.dominio.inscricao.cupom;

import java.util.Optional;

public interface CupomRepository {
    Optional<Cupom> buscarPorCodigo(String codigo);
    void atualizarUso(Cupom cupom);
}