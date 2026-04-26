package com.gestaoeventos.dominio.inscricao.cupom;

import java.util.Optional;

public interface CupomRepositorio {
    Optional<Cupom> findById(String codigo);
    Cupom save(Cupom cupom);
}
