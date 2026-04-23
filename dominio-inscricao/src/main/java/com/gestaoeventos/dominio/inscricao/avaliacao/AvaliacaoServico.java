package com.gestaoeventos.dominio.inscricao.avaliacao;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AvaliacaoServico {

    @Autowired
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    public Avaliacao salvar(int nota, String comentario, Long eventoId, String cpf) {

        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new AvaliacaoException("Evento não encontrado."));

        if (LocalDateTime.now().isBefore(evento.getDataHoraInicio())) {
            throw new AvaliacaoException("O formulário de avaliação só é desbloqueado após a realização do evento.");
        }

        boolean estaInscrito = inscricaoRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId);

        if (!estaInscrito) {
            throw new AvaliacaoException("Apenas usuários com inscrição confirmada podem avaliar.");
        }

        Pessoa autor = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new AvaliacaoException("Usuário não encontrado."));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);
        avaliacao.setEvento(evento);
        avaliacao.setPessoa(autor);

        return avaliacaoRepositorio.save(avaliacao);
    }
}
