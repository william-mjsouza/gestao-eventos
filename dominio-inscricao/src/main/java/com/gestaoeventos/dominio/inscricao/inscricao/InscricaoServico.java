package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.PessoaServico;
import com.gestaoeventos.dominio.participante.pessoa.TipoPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;

@Service
public class InscricaoServico {

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;
    @Autowired
    private EventoRepositorio eventoRepositorio;
    @Autowired
    private PessoaRepositorio pessoaRepositorio;


    @Autowired
    private PessoaServico pessoaServico;

    @Transactional
    public Inscricao iniciarInscricao(String cpf, Long eventoId, Long loteId) {
        if (inscricaoRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {
            throw new InscricaoException("Usuário já possui participação neste evento.");
        }

        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new InscricaoException("Evento não encontrado."));

        if (evento.getStatus() == StatusEvento.CANCELADO) {
            throw new InscricaoException("Não é possível se inscrever em um evento cancelado.");
        }
        if (evento.getStatus() == StatusEvento.ENCERRADO) {
            throw new InscricaoException("Não é possível se inscrever em um evento encerrado.");
        }

        long inscritos = inscricaoRepositorio.countByEventoIdAndStatusNot(eventoId, StatusInscricao.CANCELADA);
        if (inscritos >= evento.getCapacidade()) {
            throw new InscricaoException("O evento não possui mais vagas.");
        }

        Pessoa participante = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new InscricaoException("Participante não encontrado."));

        if (evento.getIdadeMinima() != null && evento.getIdadeMinima() > 0) {
            long idadeNaDataDoEvento = ChronoUnit.YEARS.between(
                    participante.getDataNascimento(),
                    evento.getDataHoraInicio().toLocalDate()
            );
            if (idadeNaDataDoEvento < evento.getIdadeMinima()) {
                throw new InscricaoException("Idade insuficiente. O participante terá " + idadeNaDataDoEvento +
                        " anos na data do evento, o que é inferior à idade mínima de " +
                        evento.getIdadeMinima() + " anos.");
            }
        }

        Lote lote = evento.getLotes().stream()
                .filter(l -> l.getId().equals(loteId) && l.getQuantidadeDisponivel() > 0)
                .findFirst()
                .orElseThrow(() -> new InscricaoException("Lote indisponível ou esgotado."));

        Inscricao inscricao = new Inscricao();
        inscricao.setParticipante(participante);
        inscricao.setEvento(evento);
        inscricao.setLote(lote);
        inscricao.setStatus(StatusInscricao.PENDENTE);

        return inscricaoRepositorio.save(inscricao);
    }

    @Transactional

    public Inscricao confirmarPagamento(Long inscricaoId, TipoPagamento tipoPagamento) {
        Inscricao inscricao = inscricaoRepositorio.findById(inscricaoId)
                .orElseThrow(() -> new InscricaoException("Inscrição não encontrada."));

        if (inscricao.getStatus() == StatusInscricao.CONFIRMADA) {
            throw new InscricaoException("Inscrição já está confirmada.");
        }

        Pessoa participante = inscricao.getParticipante();
        Lote lote = inscricao.getLote();
        double valorBase = lote.getPreco().doubleValue();

        pessoaServico.debitarSaldo(participante.getCpf(), valorBase, tipoPagamento);
        
        lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() - 1);
        inscricao.setStatus(StatusInscricao.CONFIRMADA);

        eventoRepositorio.save(inscricao.getEvento());
        return inscricaoRepositorio.save(inscricao);
    }
}