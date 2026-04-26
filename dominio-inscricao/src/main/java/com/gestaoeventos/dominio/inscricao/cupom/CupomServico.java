package com.gestaoeventos.dominio.inscricao.cupom;

import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CupomServico {

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Transactional
    public Cupom validarEAplicar(String codigoCupom, String cpfUsuario, Long eventoId) {
        Cupom cupom = cupomRepositorio.findById(codigoCupom)
                .orElseThrow(() -> new CupomException("Cupom inválido."));

        if (cupom.isEsgotado()) {
            throw new CupomException("Cupom esgotado.");
        }

        boolean jaUtilizadoPeloUsuario = inscricaoRepositorio
                .existsByParticipanteCpfAndEventoIdAndCupomCodigo(cpfUsuario, eventoId, codigoCupom);
        if (jaUtilizadoPeloUsuario) {
            throw new CupomException("Cupom já utilizado por este usuário");
        }

        cupom.registrarUso();
        return cupomRepositorio.save(cupom);
    }
}
