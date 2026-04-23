package com.gestaoeventos.dominio.inscricao.listaespera;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.compartilhado.StatusListaEspera;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
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

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Transactional
    public ListaEspera entrarNaFila(String cpf, Long eventoId) {
        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new ListaEsperaException("Evento não encontrado."));

        long inscritos = inscricaoRepositorio.countByEventoIdAndStatusNot(eventoId, StatusInscricao.CANCELADA);
        if (inscritos < evento.getCapacidade()) {
            throw new ListaEsperaException("O evento ainda possui vagas. Faça sua inscrição normalmente.");
        }

        if (listaEsperaRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {
            throw new ListaEsperaException("Você já está na lista de espera deste evento.");
        }

        Pessoa participante = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new ListaEsperaException("Participante não encontrado."));

        long totalNaFila = listaEsperaRepositorio.countByEventoIdAndStatus(eventoId, StatusListaEspera.AGUARDANDO);

        ListaEspera entrada = new ListaEspera();
        entrada.setParticipante(participante);
        entrada.setEvento(evento);
        entrada.setStatus(StatusListaEspera.AGUARDANDO);
        entrada.setPosicao((int) totalNaFila + 1);

        return listaEsperaRepositorio.save(entrada);
    }

    @Transactional
    public void processarVagaLiberada(Long eventoId) {
        List<ListaEspera> fila = listaEsperaRepositorio
                .findByEventoIdAndStatusOrderByPosicaoAsc(eventoId, StatusListaEspera.AGUARDANDO);

        if (fila.isEmpty()) {
            return;
        }

        ListaEspera primeiroNaFila = fila.get(0);
        primeiroNaFila.setStatus(StatusListaEspera.CARRINHO);
        primeiroNaFila.setDataExpiracaoCarrinho(LocalDateTime.now().plusHours(HORAS_LIMITE_PAGAMENTO));

        listaEsperaRepositorio.save(primeiroNaFila);

        enviarNotificacao(primeiroNaFila);
    }

    @Transactional
    public ListaEspera confirmarPagamento(String cpf, Long eventoId) {
        ListaEspera entrada = listaEsperaRepositorio.findByParticipanteCpfAndEventoId(cpf, eventoId)
                .orElseThrow(() -> new ListaEsperaException("Entrada na lista de espera não encontrada."));

        if (entrada.getStatus() != StatusListaEspera.CARRINHO) {
            throw new ListaEsperaException("O ingresso não está disponível no carrinho.");
        }

        if (LocalDateTime.now().isAfter(entrada.getDataExpiracaoCarrinho())) {
            expirarERepassar(entrada);
            throw new ListaEsperaException("O tempo limite para pagamento expirou. O ingresso foi repassado para o próximo da fila.");
        }

        Pessoa participante = entrada.getParticipante();
        Lote lote = entrada.getEvento().getLotes().stream()
                .findFirst()
                .orElseThrow(() -> new ListaEsperaException("Lote não encontrado para o evento."));

        double valorLote = lote.getPreco() != null ? lote.getPreco().doubleValue() : 0.0;

        if (participante.getSaldo() < valorLote) {
            throw new ListaEsperaException("Saldo insuficiente para concluir a compra.");
        }

        participante.setSaldo(participante.getSaldo() - valorLote);
        pessoaRepositorio.save(participante);

        entrada.setStatus(StatusListaEspera.CONFIRMADO);
        return listaEsperaRepositorio.save(entrada);
    }

    @Transactional
    public void expirarERepassar(ListaEspera entrada) {
        entrada.setStatus(StatusListaEspera.EXPIRADO);
        listaEsperaRepositorio.save(entrada);

        processarVagaLiberada(entrada.getEvento().getId());
    }

    private void enviarNotificacao(ListaEspera entrada) {
        System.out.println("NOTIFICAÇÃO: Olá " + entrada.getParticipante().getNome()
                + "! Um ingresso para o evento '" + entrada.getEvento().getNome()
                + "' foi adicionado ao seu carrinho. Você tem "
                + HORAS_LIMITE_PAGAMENTO + " horas para concluir o pagamento.");
    }
}
