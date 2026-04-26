package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.*;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoServico {

    @Autowired private InscricaoRepositorio inscricaoRepositorio;
    @Autowired private EventoRepositorio eventoRepositorio;
    @Autowired private PessoaRepositorio pessoaRepositorio;

    public void validarConflito(Pessoa pessoa, Evento novoEvento) {
        var conflitos = inscricaoRepositorio.buscarConflitos(
                pessoa.getCpf(),
                novoEvento.getDataHoraInicio(),
                novoEvento.getDataHoraFim()
        );

        if (!conflitos.isEmpty()) {
            throw new EventoException("Conflito de horário: participante já inscrito no evento '"
                    + conflitos.get(0).getEvento().getNome() + "'.");
        }
    }

    @Transactional
    public Inscricao iniciarInscricao(String cpf, Long eventoId, Long loteId) {
        if (inscricaoRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {
            throw new InscricaoException("Usuário já possui participação neste evento.");
        }

        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new InscricaoException("Evento não encontrado."));

        Pessoa participante = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new InscricaoException("Participante não encontrado."));

        validarConflito(participante, evento);

        if (evento.getStatus() == StatusEvento.CANCELADO) {
            throw new InscricaoException("Não é possível se inscrever em um evento cancelado.");
        }

        long inscritos = inscricaoRepositorio.countByEventoIdAndStatusNot(eventoId, StatusInscricao.CANCELADA);
        if (inscritos >= evento.getCapacidade()) {
            throw new InscricaoException("O evento não possui mais vagas.");
        }

        Lote lote = evento.getLotes().stream()
                .filter(l -> l.getId().equals(loteId))
                .findFirst()
                .orElseThrow(() -> new InscricaoException("Lote indisponível."));

        Inscricao inscricao = new Inscricao();
        inscricao.setParticipante(participante);
        inscricao.setEvento(evento);
        inscricao.setLote(lote);
        inscricao.setStatus(StatusInscricao.PENDENTE);

        return inscricaoRepositorio.save(inscricao);
    }

    @Transactional
    public Inscricao confirmarPagamento(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepositorio.findById(inscricaoId)
                .orElseThrow(() -> new InscricaoException("Inscrição não encontrada."));

        if (inscricao.getStatus() == StatusInscricao.CONFIRMADA) {
            throw new InscricaoException("Inscrição já está confirmada.");
        }

        Pessoa participante = inscricao.getParticipante();
        Lote lote = inscricao.getLote();
        double valorLote = lote.getPreco().doubleValue();

        if (participante.getSaldo() < valorLote) {
            throw new InscricaoException("Saldo insuficiente para concluir a compra");
        }

        participante.setSaldo(participante.getSaldo() - valorLote);
        lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() - 1);
        inscricao.setStatus(StatusInscricao.CONFIRMADA);

        pessoaRepositorio.save(participante);
        return inscricaoRepositorio.save(inscricao);
    }
}