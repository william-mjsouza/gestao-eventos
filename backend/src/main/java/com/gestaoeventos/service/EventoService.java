package com.gestaoeventos.service;

import com.gestaoeventos.entity.Evento;
import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.entity.StatusEvento;
import com.gestaoeventos.exception.EventoException;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public Evento salvar(Evento evento) {
        // Regra 1: Apenas organizadores podem criar eventos
        Pessoa organizador = pessoaRepository.findById(evento.getOrganizador().getCpf())
                .orElseThrow(() -> new EventoException("Organizador não encontrado no sistema."));

        if (!organizador.getOrganizador()) {
            throw new EventoException("Apenas usuários com perfil de organizador podem criar eventos.");
        }

        // Regra 2 e 3: Data futura (Anotação @Future no DTO/Entity cuida da validação de controller, mas garantimos na regra de negócio)
        if (evento.getDataHoraInicio() != null && evento.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new EventoException("A data e horário do evento devem ser futuros. Data inválida.");
        }

        // Regra 4: Pelo menos um lote
        if (evento.getLotes() == null || evento.getLotes().isEmpty()) {
            throw new EventoException("Deve haver pelo menos um lote cadastrado na publicação.");
        }

        // Regra 5: Nome único
        if (eventoRepository.existsByNome(evento.getNome())) {
            throw new EventoException("Já existe um evento cadastrado com este nome.");
        }

        return eventoRepository.save(evento);
    }

    public Evento alterarStatus(Long eventoId, StatusEvento novoStatus) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventoException("Evento não encontrado."));

        // Não pode reativar um evento cancelado ou encerrado
        if (novoStatus == StatusEvento.ATIVO) {
            if (evento.getDataHoraInicio().isBefore(LocalDateTime.now())) {
                throw new EventoException("Não é possível ativar um evento com data no passado.");
            }
            if (evento.getStatus() == StatusEvento.CANCELADO) {
                throw new EventoException("Um evento cancelado não pode ser reativado.");
            }
        }

        evento.setStatus(novoStatus);
        return eventoRepository.save(evento);
    }
}