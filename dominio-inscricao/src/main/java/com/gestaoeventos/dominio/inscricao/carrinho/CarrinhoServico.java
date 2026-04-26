package com.gestaoeventos.dominio.inscricao.carrinho;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CarrinhoServico {

    @Autowired
    private CarrinhoRepositorio carrinhoRepositorio;
    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;
    @Autowired
    private EventoRepositorio eventoRepositorio;
    @Autowired
    private PessoaRepositorio pessoaRepositorio;
    @Autowired
    private LoteServico loteServico;

    @Transactional
    public Carrinho adicionarAoCarrinho(String cpf, Long eventoId, Long loteId) {
        if (inscricaoRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId) ||
                carrinhoRepositorio.existsByParticipanteCpfAndEventoId(cpf, eventoId)) {
            throw new CarrinhoException("Usuário já possui ingresso ou item no carrinho para este evento.");
        }

        Evento evento = eventoRepositorio.findById(eventoId)
                .orElseThrow(() -> new CarrinhoException("Evento não encontrado."));

        Pessoa participante = pessoaRepositorio.findById(cpf)
                .orElseThrow(() -> new CarrinhoException("Participante não encontrado."));

        Lote lote = evento.getLotes().stream()
                .filter(l -> l.getId().equals(loteId) && l.getQuantidadeDisponivel() > 0)
                .findFirst()
                .orElseThrow(() -> new CarrinhoException("Lote indisponível ou esgotado."));

        Carrinho carrinho = new Carrinho();
        carrinho.setParticipante(participante);
        carrinho.setEvento(evento);
        carrinho.setLote(lote);

        return carrinhoRepositorio.save(carrinho);
    }

    @Transactional
    public Inscricao finalizarCompra(Long carrinhoId) {
        Carrinho carrinho = carrinhoRepositorio.findById(carrinhoId)
                .orElseThrow(() -> new CarrinhoException("Carrinho não encontrado."));

        Lote loteNoCarrinho = carrinho.getLote();

        // Verifica se o lote no carrinho expirou por tempo ou vagas
        boolean loteExpirado = loteNoCarrinho.getQuantidadeDisponivel() <= 0 ||
                LocalDateTime.now().isAfter(loteNoCarrinho.getDataFimVenda());

        if (loteExpirado) {
            // Dinâmica de Lotes: Busca o lote que está valendo agora
            Lote novoLoteAtivo = loteServico.obterLoteAtivo(carrinho.getEvento().getId());

            // Atualiza o carrinho para o novo lote e salva
            carrinho.setLote(novoLoteAtivo);
            carrinhoRepositorio.save(carrinho);

            // Dispara o alerta para o usuário
            throw new CarrinhoException("O lote virou! O valor foi atualizado para o "
                    + novoLoteAtivo.getNome() + " (R$ " + novoLoteAtivo.getPreco() + ") antes de cobrar.");
        }

        Pessoa participante = carrinho.getParticipante();
        double valorLote = loteNoCarrinho.getPreco().doubleValue();

        if (participante.getSaldo() < valorLote) {
            throw new CarrinhoException("Saldo insuficiente para concluir a compra.");
        }

        // 1. Desconta o saldo e a vaga
        participante.setSaldo(participante.getSaldo() - valorLote);
        loteNoCarrinho.setQuantidadeDisponivel(loteNoCarrinho.getQuantidadeDisponivel() - 1);

        // 2. Gera a Inscrição definitiva (O ingresso real)
        Inscricao inscricao = new Inscricao();
        inscricao.setParticipante(participante);
        inscricao.setEvento(carrinho.getEvento());
        inscricao.setLote(loteNoCarrinho);
        inscricao.setStatus(StatusInscricao.CONFIRMADA);
        inscricao.setDataReserva(LocalDateTime.now());

        // 3. Salva tudo e limpa o carrinho
        pessoaRepositorio.save(participante);
        eventoRepositorio.save(carrinho.getEvento());
        Inscricao inscricaoSalva = inscricaoRepositorio.save(inscricao);

        carrinhoRepositorio.delete(carrinho); // Esvazia o carrinho após a compra com sucesso

        return inscricaoSalva;
    }
}