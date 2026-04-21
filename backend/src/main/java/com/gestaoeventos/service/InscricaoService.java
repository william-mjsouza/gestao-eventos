package com.gestaoeventos.service;

import com.gestaoeventos.entity.*;
import com.gestaoeventos.exception.InscricaoException;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.InscricaoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    @Autowired
    private InscricaoRepository inscricaoRepository;
    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private PessoaRepository pessoaRepository;

    @Transactional
    public Inscricao iniciarInscricao(String cpf, Long eventoId, Long loteId) {
        if (inscricaoRepository.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {

            throw new InscricaoException("Usuário já possui participação neste evento.");
        }

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new InscricaoException("Evento não encontrado."));

        if (evento.getStatus() == StatusEvento.CANCELADO) {
            throw new InscricaoException("Não é possível se inscrever em um evento cancelado.");
        }
        if (evento.getStatus() == StatusEvento.ENCERRADO) {
            throw new InscricaoException("Não é possível se inscrever em um evento encerrado.");
        }

        long inscritos = inscricaoRepository.countByEventoId(eventoId);
        if (inscritos >= evento.getCapacidade()) {
            throw new InscricaoException("O evento não possui mais vagas.");
        }

        Pessoa participante = pessoaRepository.findById(cpf)
                .orElseThrow(() -> new InscricaoException("Participante não encontrado."));

        Lote lote = evento.getLotes().stream()
                .filter(l -> l.getId().equals(loteId) && l.getQuantidadeDisponivel() > 0)
                .findFirst()
                .orElseThrow(() -> new InscricaoException("Lote indisponível ou esgotado."));

        Inscricao inscricao = new Inscricao();
        inscricao.setParticipante(participante);
        inscricao.setEvento(evento);
        inscricao.setLote(lote);
        inscricao.setStatus(StatusInscricao.PENDENTE);

        return inscricaoRepository.save(inscricao);
    }

    @Transactional
    public Inscricao confirmarPagamento(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
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

        pessoaRepository.save(participante);
        eventoRepository.save(inscricao.getEvento());
        return inscricaoRepository.save(inscricao);
    }
}