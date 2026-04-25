package com.gestaoeventos.dominio.evento.evento;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventoServico {

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    public Evento salvar(Evento evento) {
        Pessoa organizador = pessoaRepositorio.findById(evento.getOrganizador().getCpf())
                .orElseThrow(() -> new EventoException("Organizador não encontrado no sistema."));

        if (!organizador.getOrganizador()) {
            throw new EventoException("Apenas usuários com perfil de organizador podem criar eventos.");
        }

        if (evento.getDataHoraInicio() != null && evento.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new EventoException("A data e horário do evento devem ser futuros. Data inválida.");
        }

        if (evento.getDataHoraTermino() == null) {
            throw new EventoException("A data de término é obrigatória.");
        }

        if (evento.getDataHoraTermino().isBefore(evento.getDataHoraInicio()) || evento.getDataHoraTermino().isEqual(evento.getDataHoraInicio())) {
            throw new EventoException("A data de término deve ser posterior à data de início.");
        }

        if (eventoRepositorio.existeColisaoLocalEHorario(evento.getLocal(), evento.getDataHoraInicio(), evento.getDataHoraTermino())) {
            throw new EventoException("O local já está ocupado neste período.");
        }

        if (evento.getLotes() == null || evento.getLotes().isEmpty()) {
            throw new EventoException("Deve haver pelo menos um lote cadastrado na publicação.");
        }

        int totalVagasLotes = evento.getLotes().stream().mapToInt(l -> l.getQuantidadeTotal()).sum();
        if (totalVagasLotes > evento.getCapacidade()) {
            throw new EventoException("A soma das vagas dos lotes (" + totalVagasLotes
                    + ") ultrapassa a capacidade máxima do evento (" + evento.getCapacidade() + ").");
        }

        if (eventoRepositorio.existsByNome(evento.getNome())) {
            throw new EventoException("Já existe um evento cadastrado com este nome.");
        }

        return eventoRepositorio.save(evento);
    }

    public Evento alterarStatus(Long eventoId, StatusEvento novoStatus) {
        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new EventoException("Evento não encontrado."));

        if (novoStatus == StatusEvento.ATIVO) {
            if (evento.getDataHoraInicio().isBefore(LocalDateTime.now())) {
                throw new EventoException("Não é possível ativar um evento com data no passado.");
            }
            if (evento.getStatus() == StatusEvento.CANCELADO) {
                throw new EventoException("Um evento cancelado não pode ser reativado.");
            }
        }

        evento.setStatus(novoStatus);
        return eventoRepositorio.save(evento);
    }
}
