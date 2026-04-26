package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.listaespera.ListaEsperaServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.PessoaServico; // Importe o serviço
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelamentoInscricaoServico {

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

        LocalDateTime dataLimiteCancelamento = inscricao.getEvento().getDataHoraInicio().minusHours(24);
        if (LocalDateTime.now().isAfter(dataLimiteCancelamento)) {
            throw new InscricaoException("\"Não é possível cancelar, fora do prazo\"");
        }

        boolean eraConfirmada = inscricao.getStatus() == StatusInscricao.CONFIRMADA;
        if (eraConfirmada) {
            Pessoa participante = inscricao.getParticipante();
            Lote lote = inscricao.getLote();

            double valorLote = lote.getPreco() != null ? lote.getPreco().doubleValue() : 0.0;

            pessoaServico.estornarSaldo(participante.getCpf(), valorLote);
            
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