package com.gestaoeventos.service;

import com.gestaoeventos.entity.*;
import com.gestaoeventos.exception.ListaEsperaException;
import com.gestaoeventos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ListaEsperaService {

    // Tempo limite em horas para o usuário pagar após o ingresso entrar no carrinho
    public static final int HORAS_LIMITE_PAGAMENTO = 2;

    @Autowired
    private ListaEsperaRepository listaEsperaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private InscricaoRepository inscricaoRepository;


    @Transactional
    public ListaEspera entrarNaFila(String cpf, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ListaEsperaException("Evento não encontrado."));


        long inscritos = inscricaoRepository.countByEventoId(eventoId);
        if (inscritos < evento.getCapacidade()) {
            throw new ListaEsperaException("O evento ainda possui vagas. Faça sua inscrição normalmente.");
        }


        if (listaEsperaRepository.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {
            throw new ListaEsperaException("Você já está na lista de espera deste evento.");
        }

        Pessoa participante = pessoaRepository.findById(cpf)
                .orElseThrow(() -> new ListaEsperaException("Participante não encontrado."));

        long totalNaFila = listaEsperaRepository.countByEventoIdAndStatus(eventoId, StatusListaEspera.AGUARDANDO);

        ListaEspera entrada = new ListaEspera();
        entrada.setParticipante(participante);
        entrada.setEvento(evento);
        entrada.setStatus(StatusListaEspera.AGUARDANDO);
        entrada.setPosicao((int) totalNaFila + 1);

        return listaEsperaRepository.save(entrada);
    }


    @Transactional
    public void processarVagaLiberada(Long eventoId) {
        List<ListaEspera> fila = listaEsperaRepository
                .findByEventoIdAndStatusOrderByPosicaoAsc(eventoId, StatusListaEspera.AGUARDANDO);

        if (fila.isEmpty()) {
            return;
        }

        ListaEspera primeiroNaFila = fila.get(0);
        primeiroNaFila.setStatus(StatusListaEspera.CARRINHO);
        primeiroNaFila.setDataExpiracaoCarrinho(LocalDateTime.now().plusHours(HORAS_LIMITE_PAGAMENTO));

        listaEsperaRepository.save(primeiroNaFila);


        enviarNotificacao(primeiroNaFila);
    }

    @Transactional
    public ListaEspera confirmarPagamento(String cpf, Long eventoId) {
        ListaEspera entrada = listaEsperaRepository.findByParticipanteCpfAndEventoId(cpf, eventoId)
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
        pessoaRepository.save(participante);

        entrada.setStatus(StatusListaEspera.CONFIRMADO);
        return listaEsperaRepository.save(entrada);
    }

    @Transactional
    public void expirarERepassar(ListaEspera entrada) {
        entrada.setStatus(StatusListaEspera.EXPIRADO);
        listaEsperaRepository.save(entrada);


        processarVagaLiberada(entrada.getEvento().getId());
    }

    private void enviarNotificacao(ListaEspera entrada) {
        System.out.println("NOTIFICAÇÃO: Olá " + entrada.getParticipante().getNome()
                + "! Um ingresso para o evento '" + entrada.getEvento().getNome()
                + "' foi adicionado ao seu carrinho. Você tem "
                + HORAS_LIMITE_PAGAMENTO + " horas para concluir o pagamento.");
    }
}