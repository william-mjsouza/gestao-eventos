package com.gestaoeventos.dominio.inscricao.avaliacao;

import com.gestaoeventos.dominio.compartilhado.StatusEvento;
import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AvaliacaoServico {

    public static final double VALOR_CASHBACK = 5.0;

    @Autowired
    private AvaliacaoRepositorio avaliacaoRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Transactional
    public Avaliacao salvar(int nota, String comentario, Long eventoId, String cpf) {

        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new AvaliacaoException("Evento não encontrado."));

        if (evento.getStatus() != StatusEvento.ENCERRADO) {
            throw new AvaliacaoException("O formulário de avaliação só é desbloqueado após o encerramento do evento.");
        }

        if (LocalDateTime.now().isBefore(evento.getDataHoraInicio())) {
            throw new AvaliacaoException("O formulário de avaliação só é desbloqueado após a realização do evento.");
        }

        boolean estaInscritoConfirmado = inscricaoRepositorio
                .existsByParticipanteCpfAndEventoIdAndStatus(cpf, eventoId, StatusInscricao.CONFIRMADA);

        if (!estaInscritoConfirmado) {
            throw new AvaliacaoException("Apenas usuários com inscrição confirmada podem avaliar.");
        }

        if (avaliacaoRepositorio.existsByPessoaCpfAndEventoId(cpf, eventoId)) {
            throw new AvaliacaoException("Você já avaliou este evento anteriormente.");
        }

        Pessoa autor = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new AvaliacaoException("Usuário não encontrado."));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);
        avaliacao.setEvento(evento);
        avaliacao.setPessoa(autor);

        Avaliacao salva = avaliacaoRepositorio.save(avaliacao);

        autor.setSaldo(autor.getSaldo() + VALOR_CASHBACK);
        pessoaRepositorio.save(autor);

        System.out.println("Obrigado! Você ganhou R$ "
                + String.format("%.2f", VALOR_CASHBACK)
                + " de cashback pela avaliação.");

        return salva;
    }
}