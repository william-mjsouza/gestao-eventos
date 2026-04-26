package com.gestaoeventos.infraestrutura.persistencia.jpa;

import com.gestaoeventos.aplicacao.NotificarFilaUseCase;
import com.gestaoeventos.dominio.inscricao.inscricao.evento.VagaLiberadaFilaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoListener {

    private final NotificarFilaUseCase notificarFilaUseCase;

    public NotificacaoListener(NotificarFilaUseCase notificarFilaUseCase) {
        this.notificarFilaUseCase = notificarFilaUseCase;
    }

    @EventListener
    public void onVagaLiberada(VagaLiberadaFilaEvent evento) {
        notificarFilaUseCase.executar(evento);
    }
}