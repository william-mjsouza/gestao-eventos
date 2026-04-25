package com.gestaoeventos.aplicacao;

import com.gestaoeventos.dominio.inscricao.inscricao.evento.VagaLiberadaFilaEvent;

public class NotificarFilaUseCase {

    public void executar(VagaLiberadaFilaEvent evento) {
        // Formata e envia notificação "Vaga Liberada - Prazo de 2 horas"
        System.out.println("Notificando usuário " + evento.getUsuarioId()
                + " sobre vaga liberada no lote " + evento.getLoteId());
    }
}