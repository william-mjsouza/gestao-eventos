package com.gestaoeventos.dominio.inscricao.inscricao;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoServico {

    private final PessoaRepositorio participanteRepo;
    private final EventoRepositorio eventoRepo;
    private final InscricaoRepositorio inscricaoRepo;

    public InscricaoServico(PessoaRepositorio participanteRepo,
                            EventoRepositorio eventoRepo,
                            InscricaoRepositorio inscricaoRepo) {
        this.participanteRepo = participanteRepo;
        this.eventoRepo = eventoRepo;
        this.inscricaoRepo = inscricaoRepo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void realizarInscricao(String cpf, Long eventoId, Long loteId) {


        if (inscricaoRepo.jaInscrito(cpf, eventoId)) {
            throw new InscricaoException("Participante já possui inscrição para este evento.");
        }


        Pessoa pessoa = participanteRepo.buscarPorCpf(cpf)
                .orElseThrow(() -> new InscricaoException("Participante não encontrado."));

        Evento evento = eventoRepo.buscarPorId(eventoId)
                .orElseThrow(() -> new InscricaoException("Evento não encontrado."));

        Lote lote = evento.getLote(loteId);


        lote.baixarVaga();
        pessoa.debitarSaldo(lote.getPreco());


        participanteRepo.atualizarSaldo(pessoa);
        eventoRepo.atualizarVagasLote(lote.getId(), lote.getQuantidadeDisponivel());


        try {
            Inscricao novaInscricao = new Inscricao(cpf, eventoId);
            inscricaoRepo.salvar(novaInscricao);
        } catch (Exception e) {

            throw new InscricaoException("Falha ao gerar ingresso. Operação revertida.");
        }
    }
}