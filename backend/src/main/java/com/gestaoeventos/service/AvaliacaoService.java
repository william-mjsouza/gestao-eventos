package com.gestaoeventos.service;

import com.gestaoeventos.entity.Avaliacao;
import com.gestaoeventos.entity.Evento;
import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.exception.AvaliacaoException;
import com.gestaoeventos.repository.AvaliacaoRepository;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.InscricaoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;
    public Avaliacao salvar(int nota, String comentario, Long eventoId, String cpf) {

        // 1. Verificar se o evento existe
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new AvaliacaoException("Evento não encontrado."));


        if (LocalDateTime.now().isBefore(evento.getDataHoraInicio())) {
            throw new AvaliacaoException("O formulário de avaliação só é desbloqueado após a realização do evento.");
        }

        // 3. Regra de Negócio: O usuário tem inscrição confirmada?
        boolean estaInscrito = inscricaoRepository.existsByParticipanteCpfAndEventoId(cpf, eventoId);

        if (!estaInscrito) {
            throw new AvaliacaoException("Apenas usuários com inscrição confirmada podem avaliar.");
        }

        // 4. Buscar a pessoa para vincular à avaliação
        Pessoa autor = pessoaRepository.findById(cpf)
                .orElseThrow(() -> new AvaliacaoException("Usuário não encontrado."));

        // 5. Criar e salvar a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);
        avaliacao.setEvento(evento);
        avaliacao.setPessoa(autor);

        return avaliacaoRepository.save(avaliacao);
    }
}