package com.gestaoeventos.dominio.inscricao.cupom;

import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;

public class CupomServico {

    private final CupomRepository cupomRepository;
    private final InscricaoRepositorio inscricaoRepositorio;

    public CupomServico(CupomRepository cupomRepository, InscricaoRepositorio inscricaoRepositorio) {
        this.cupomRepository = cupomRepository;
        this.inscricaoRepositorio = inscricaoRepositorio;
    }

    public Cupom validarEAplicar(String codigoCupom, String cpfUsuario, Long eventoId) {

        Cupom cupom = cupomRepository.buscarPorCodigo(codigoCupom)
                .orElseThrow(() -> new CupomException("Cupom inválido."));


        if (cupom.isEsgotado()) {
            throw new CupomException("Cupom esgotado");
        }


        boolean jaUtilizado = inscricaoRepositorio.existeUsoDeCupomPorCpfEEvento(cpfUsuario, codigoCupom, eventoId);
        if (jaUtilizado) {
            throw new CupomException("Cupom já utilizado por este usuário");
        }


        cupom.registrarUso();


        cupomRepository.atualizarUso(cupom);

        return cupom;
    }
}