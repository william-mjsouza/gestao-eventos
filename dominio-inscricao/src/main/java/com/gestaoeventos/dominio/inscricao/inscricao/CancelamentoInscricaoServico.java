package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.PessoaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class CancelamentoInscricaoServico {

    private static final long HORAS_LIMITE_CANCELAMENTO = 48;
    private static final long HORAS_ESTORNO_INTEGRAL = 168; // 7 dias

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Autowired
    private ListaEsperaServico listaEsperaServico;

    @Autowired
    private PessoaServico pessoaServico;

    @Transactional
    public Inscricao executar(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepositorio.findById(inscricaoId)
                .orElseThrow(() -> new InscricaoException("Inscrição não encontrada."));

        if (inscricao.getStatus() == StatusInscricao.CANCELADA) {
            throw new InscricaoException("Esta inscrição já se encontra cancelada.");
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataEvento = inscricao.getEvento().getDataHoraInicio();
        long horasRestantes = ChronoUnit.HOURS.between(agora, dataEvento);

        if (horasRestantes < HORAS_LIMITE_CANCELAMENTO) {
            throw new InscricaoException("Não é possível cancelar com menos de 48 horas de antecedência.");
        }

        boolean eraConfirmada = inscricao.getStatus() == StatusInscricao.CONFIRMADA;
        if (eraConfirmada) {
            Pessoa participante = inscricao.getParticipante();
            Lote lote = inscricao.getLote();

            double valorPago = lote.getPreco() != null ? lote.getPreco().doubleValue() : 0.0;
            double percentual = horasRestantes > HORAS_ESTORNO_INTEGRAL ? 1.0 : 0.5;
            double valorEstorno = valorPago * percentual;

            pessoaServico.estornarSaldo(participante.getCpf(), valorEstorno);

            lote.setQuantidadeDisponivel(lote.getQuantidadeDisponivel() + 1);
            eventoRepositorio.save(inscricao.getEvento());
        }

        inscricao.setStatus(StatusInscricao.CANCELADA);
        Inscricao cancelada = inscricaoRepositorio.save(inscricao);

        if (eraConfirmada) {
            listaEsperaServico.processarVagaLiberada(inscricao.getEvento().getId());
        }

        return cancelada;
    }
}