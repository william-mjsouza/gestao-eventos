package com.gestaoeventos.dominio.inscricao.listaespera;

import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListaEsperaServico {

    public static final int HORAS_LIMITE_PAGAMENTO = 2;

    @Autowired
    private ListaEsperaRepositorio listaEsperaRepositorio;

    @Autowired
    private EventoRepositorio eventoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @Transactional
    public ListaEspera entrarNaFila(String cpf, Long eventoId) {
        if (listaEsperaRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {
            throw new ListaEsperaException("Usuário já está na lista de espera.");
        }

        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new ListaEsperaException("Evento não encontrado."));

        Pessoa pessoa = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new ListaEsperaException("Participante não encontrado."));

        long totalNaFila = listaEsperaRepositorio.countByEventoIdAndStatus(eventoId, StatusListaEspera.AGUARDANDO);

        ListaEspera entrada = new ListaEspera();
        entrada.setParticipante(pessoa);
        entrada.setEvento(evento);
        entrada.setPosicao((int) totalNaFila + 1);
        entrada.setStatus(StatusListaEspera.AGUARDANDO);

        return listaEsperaRepositorio.save(entrada);
    }

    @Transactional
    public ListaEspera confirmarPagamento(String cpf, Long eventoId) {
        ListaEspera entrada = listaEsperaRepositorio.findByParticipanteCpfAndEventoId(cpf, eventoId)
                .orElseThrow(() -> new ListaEsperaException("Entrada na lista de espera não encontrada."));

        if (entrada.getStatus() != StatusListaEspera.CARRINHO) {
            throw new ListaEsperaException("O item não está disponível para pagamento no carrinho.");
        }

        // REGRA DE NEGÓCIO: Verificação de TTL (2 horas)
        if (entrada.getDataExpiracaoCarrinho().isBefore(LocalDateTime.now())) {
            processarExpiracaoERepasse(entrada);
            throw new ListaEsperaException("Prazo de pagamento expirado. A vaga foi repassada para o próximo da fila.");
        }

        entrada.setStatus(StatusListaEspera.CONFIRMADO);
        return listaEsperaRepositorio.save(entrada);
    }

    // =========================================================================
    // NOVO MÉTODO: Processa qualquer vaga liberada (por cancelamento ou TTL)
    // =========================================================================
    @Transactional
    public void processarVagaLiberada(Long eventoId) {
        // Busca o próximo da fila (FIFO)
        List<ListaEspera> fila = listaEsperaRepositorio
                .findByEventoIdAndStatusOrderByPosicaoAsc(eventoId, StatusListaEspera.AGUARDANDO);

        if (!fila.isEmpty()) {
            ListaEspera proximo = fila.get(0);
            proximo.setStatus(StatusListaEspera.CARRINHO);
            proximo.setDataExpiracaoCarrinho(LocalDateTime.now().plusHours(HORAS_LIMITE_PAGAMENTO));
            listaEsperaRepositorio.save(proximo);
        }
    }

    private void processarExpiracaoERepasse(ListaEspera entradaExpirada) {
        // 1. Marca quem perdeu o prazo como expirado
        entradaExpirada.setStatus(StatusListaEspera.EXPIRADO);
        listaEsperaRepositorio.save(entradaExpirada);

        // 2. Chama o método que acabamos de criar para repassar a vaga
        processarVagaLiberada(entradaExpirada.getEvento().getId());
    }
}