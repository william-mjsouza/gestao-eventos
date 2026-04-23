package com.gestaoeventos.dominio.evento.lote;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoteServico {

    @Autowired
    private EventoRepositorio eventoRepositorio;

    public Lote obterLoteAtivo(Long eventoId) {
        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new LoteException("Evento não encontrado."));

        LocalDateTime agora = LocalDateTime.now();

        return evento.getLotes().stream()
                .filter(lote -> lote.getQuantidadeDisponivel() > 0)
                .filter(lote -> !agora.isBefore(lote.getDataInicioVenda()) && !agora.isAfter(lote.getDataFimVenda()))
                .findFirst()
                .orElseThrow(() -> new LoteException("Não há lotes disponíveis no momento para este evento."));
    }
}
