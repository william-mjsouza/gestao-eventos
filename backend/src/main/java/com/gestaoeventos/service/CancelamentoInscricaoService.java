package com.gestaoeventos.service;

import com.gestaoeventos.entity.Inscricao;
import com.gestaoeventos.entity.Lote;
import com.gestaoeventos.entity.Pessoa;
import com.gestaoeventos.entity.StatusInscricao;
import com.gestaoeventos.exception.InscricaoException;
import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.InscricaoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelamentoInscricaoService {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ListaEsperaService listaEsperaService;

    @Transactional
    public Inscricao executar(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new InscricaoException("Inscrição não encontrada."));

        if (inscricao.getStatus() == StatusInscricao.CANCELADA) {
            throw new InscricaoException("Esta inscrição já se encontra cancelada.");
        }

        // Cancelamento apenas até 24h antes do evento
        LocalDateTime dataLimiteCancelamento = inscricao.getEvento().getDataHoraInicio().minusHours(24);
        if (LocalDateTime.now().isAfter(dataLimiteCancelamento)) {
            throw new InscricaoException("\"Não é possível cancelar, fora do prazo\"");
        }

        // Se a inscrição já foi paga (CONFIRMADA), fazemos o estorno financeiro e devolução da vaga
        boolean eraConfirmada = inscricao.getStatus() == StatusInscricao.CONFIRMADA;
        if (eraConfirmada) {
            Pessoa participante = inscricao.getParticipante();
            Lote lote = inscricao.getLote();

            double valorLote = lote.getPreco() != null ? lote.getPreco().doubleValue() : 0.0;

            participante.setSaldo(participante.getSaldo() + valorLote);
            lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() + 1);

            pessoaRepository.save(participante);
            eventoRepository.save(inscricao.getEvento());
        }

        inscricao.setStatus(StatusInscricao.CANCELADA);
        Inscricao cancelada = inscricaoRepository.save(inscricao);

        // Vaga liberada: notifica o próximo na lista de espera
        if (eraConfirmada) {
            listaEsperaService.processarVagaLiberada(inscricao.getEvento().getId());
        }

        return cancelada;
    }
}