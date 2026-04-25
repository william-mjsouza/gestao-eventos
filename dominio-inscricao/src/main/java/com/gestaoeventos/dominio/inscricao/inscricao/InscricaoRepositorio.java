package com.gestaoeventos.dominio.inscricao.inscricao;


public interface InscricaoRepositorio {
    void salvar(Inscricao inscricao);
    boolean jaInscrito(String cpf, Long eventoId);
    boolean existsByParticipanteCpfAndEventoId(String cpf,Long eventoId);
    boolean existeUsoDeCupomPorCpfEEvento(String cpfUsuario,String codigoCupom,Long eventoId);
}