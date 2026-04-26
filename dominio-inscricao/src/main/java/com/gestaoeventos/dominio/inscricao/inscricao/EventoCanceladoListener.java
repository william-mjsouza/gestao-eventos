package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.evento.evento.EventoCanceladoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventoCanceladoListener {

    @Autowired
    private CancelamentoEmCascataServico cancelamentoEmCascataServico;

    // Executa após o commit do cancelamento do evento, garantindo desacoplamento transacional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void aoEventoCancelado(EventoCanceladoEvent evento) {
        cancelamentoEmCascataServico.executar(evento.eventoId());
    }
}
