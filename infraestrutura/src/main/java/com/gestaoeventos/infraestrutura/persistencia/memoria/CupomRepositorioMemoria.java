package com.gestaoeventos.infraestrutura.persistencia.memoria;

import com.gestaoeventos.dominio.inscricao.cupom.Cupom;
import com.gestaoeventos.dominio.inscricao.cupom.CupomRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CupomRepositorioMemoria implements CupomRepositorio {

    private final Map<String, Cupom> armazenamento = new ConcurrentHashMap<>();

    @Override
    public Optional<Cupom> findById(String codigo) {
        return Optional.ofNullable(armazenamento.get(codigo));
    }

    @Override
    public Cupom save(Cupom cupom) {
        armazenamento.put(cupom.getCodigo(), cupom);
        return cupom;
    }
}
