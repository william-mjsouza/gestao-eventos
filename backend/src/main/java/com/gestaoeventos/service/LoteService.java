package com.gestaoeventos.service;

import com.gestaoeventos.entity.Evento;
import com.gestaoeventos.entity.Lote;
import com.gestaoeventos.exception.LoteException;
import com.gestaoeventos.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoteService {

    @Autowired
    private EventoRepository eventoRepository;

    public Lote obterLoteAtivo(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new LoteException("Evento não encontrado."));

        LocalDateTime agora = LocalDateTime.now();

        // Filtra o primeiro lote que tenha vaga E esteja dentro do prazo de vendas
        return evento.getLotes().stream()
                .filter(lote -> lote.getQuantidadeDisponivel() > 0)
                .filter(lote -> !agora.isBefore(lote.getDataInicioVenda()) && !agora.isAfter(lote.getDataFimVenda()))
                .findFirst()
                .orElseThrow(() -> new LoteException("Não há lotes disponíveis no momento para este evento."));
    }
}