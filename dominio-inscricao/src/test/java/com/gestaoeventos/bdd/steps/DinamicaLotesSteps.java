package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.evento.lote.LoteServico;
import com.gestaoeventos.dominio.inscricao.carrinho.Carrinho;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoException;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoRepositorio;
import com.gestaoeventos.dominio.inscricao.carrinho.CarrinhoServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DinamicaLotesSteps {

    @Autowired
    private CarrinhoServico carrinhoServico;

    @Autowired
    private CarrinhoRepositorio carrinhoRepositorio;

    @Autowired
    private LoteServico loteServico;

    private Evento evento;
    private Lote lote1;
    private Lote lote2;
    private Carrinho carrinho;
    private Exception excecaoCapturada;

    @Dado("que o usuário tem 1 ingresso do Lote 1 no carrinho com valor de R$ {double}")
    public void usuario_tem_ingresso_no_carrinho(Double valorLote1) {
        Pessoa participante = new Pessoa();
        participante.setCpf("11122233344");
        participante.setSaldo(200.0);

        evento = new Evento();
        evento.setId(100L);
        evento.setLotes(new ArrayList<>());

        lote1 = new Lote(1L, "Lote 1", BigDecimal.valueOf(valorLote1), 10, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5));
        lote2 = new Lote(2L, "Lote 2", BigDecimal.valueOf(70.0), 20, 20, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10));

        evento.getLotes().add(lote1);
        evento.getLotes().add(lote2);

        // Criando a entidade Carrinho
        carrinho = new Carrinho(1L, participante, evento, lote1, LocalDateTime.now());

        when(carrinhoRepositorio.findById(1L)).thenReturn(Optional.of(carrinho));
    }

    @E("o Lote 1 esgota as vagas no sistema enquanto ele navega")
    public void lote1_esgota_vagas() {
        // Zera a quantidade para forçar a virada de lote
        lote1.setQuantidadeDisponivel(0);

        // Mock: quando o serviço procurar o próximo lote válido, retorna o Lote 2
        when(loteServico.obterLoteAtivo(evento.getId())).thenReturn(lote2);
    }

    @Quando("o usuário avança para a tela final de pagamento")
    public void usuario_avanca_pagamento() {
        try {
            carrinhoServico.finalizarCompra(1L);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Entao("o sistema deve informar que o lote virou")
    public void sistema_informa_lote_virou() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria bloquear o pagamento devido à virada do lote");
        assertTrue(excecaoCapturada instanceof CarrinhoException);
        assertTrue(excecaoCapturada.getMessage().contains("O lote virou!"),
                "A mensagem deveria informar sobre a virada do lote");
    }

    @E("atualizar o valor para o Lote 2 com valor de R$ {double} antes de cobrar")
    public void atualizar_valor_novo_lote(Double valorLote2) {
        // Verifica se a entidade Carrinho foi atualizada na memória
        assertEquals(lote2.getId(), carrinho.getLote().getId(), "O carrinho deveria ter o Lote 2 associado");
        assertEquals(BigDecimal.valueOf(valorLote2), carrinho.getLote().getPreco(), "O preço no carrinho deveria refletir o novo lote");

        // Verifica se a atualização foi persistida no banco
        verify(carrinhoRepositorio, times(1)).save(carrinho);
    }
}