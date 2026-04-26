package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.participante.pessoa.PessoaServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CancelamentoEmCascataServico {

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private PessoaServico pessoaServico;

    private static final List<StatusInscricao> STATUSES_ATIVAS = List.of(
            StatusInscricao.PENDENTE,
            StatusInscricao.CONFIRMADA
    );

    @Transactional
    public void executar(Long eventoId) {
        List<Inscricao> inscricoesAtivas = inscricaoRepositorio
                .findByEventoIdAndStatusIn(eventoId, STATUSES_ATIVAS);

        for (Inscricao inscricao : inscricoesAtivas) {
            boolean eraConfirmada = inscricao.getStatus() == StatusInscricao.CONFIRMADA;

            if (eraConfirmada) {
                double valorEstorno = inscricao.getLote().getPreco().doubleValue();
                pessoaServico.estornarSaldo(inscricao.getParticipante().getCpf(), valorEstorno);
            }

            inscricao.setStatus(StatusInscricao.CANCELADA_PELO_ORGANIZADOR);
            inscricaoRepositorio.save(inscricao);

            notificarParticipante(inscricao, eraConfirmada);
        }
    }

    private void notificarParticipante(Inscricao inscricao, boolean comEstorno) {
        String mensagem = "[NOTIFICAÇÃO] Inscrição #" + inscricao.getId()
                + " do participante " + inscricao.getParticipante().getNome()
                + " foi cancelada. O evento foi cancelado pelo organizador.";
        if (comEstorno) {
            mensagem += " Estorno de R$ " + inscricao.getLote().getPreco() + " processado.";
        }
        System.out.println(mensagem);
    }
}
