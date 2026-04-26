package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.compartilhado.StatusInscricao;
import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.cupom.Cupom;
import com.gestaoeventos.dominio.inscricao.cupom.CupomException;
import com.gestaoeventos.dominio.inscricao.cupom.CupomRepositorio;
import com.gestaoeventos.dominio.inscricao.cupom.CupomServico;
import com.gestaoeventos.dominio.inscricao.inscricao.Inscricao;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.TipoPagamento;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CupomSteps {

    private static final String CPF_USUARIO = "11122233344";
    private static final Long EVENTO_ID = 1L;
    private static final Long INSCRICAO_ID = 555L;

    @Autowired
    private CupomServico cupomServico;

    @Autowired
    private InscricaoServico inscricaoServico;

    @Autowired
    private CupomRepositorio cupomRepositorio;

    @Autowired
    private InscricaoRepositorio inscricaoRepositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Cupom cupom;
    private Pessoa participante;
    private Evento evento;
    private Lote lote;
    private Inscricao inscricaoPendente;
    private Inscricao inscricaoConfirmada;
    private Exception excecao;

    // ----- Cenário: Uso único por CPF bloqueado -----

    @Dado("que o cupom {string} possui a regra de 1 uso por CPF")
    public void cupom_com_regra_uso_unico(String codigo) {
        excecao = null;
        cupom = new Cupom(codigo, 100, 1);
        when(cupomRepositorio.findById(codigo)).thenReturn(Optional.of(cupom));
    }

    @E("o usuário já realizou uma compra anterior utilizando este cupom")
    public void usuario_ja_utilizou_cupom() {
        when(inscricaoRepositorio
                .existsByParticipanteCpfAndEventoIdAndCupomCodigo(CPF_USUARIO, EVENTO_ID, cupom.getCodigo()))
                .thenReturn(true);
    }

    @Quando("o usuário tenta aplicar {string} novamente em um novo carrinho")
    public void usuario_tenta_aplicar_cupom(String codigo) {
        try {
            cupomServico.validarEAplicar(codigo, CPF_USUARIO, EVENTO_ID);
        } catch (Exception e) {
            excecao = e;
        }
    }

    // ----- Cenário: Cupom esgotado -----

    @Dado("que o cupom {string} atingiu o limite total de usos")
    public void cupom_esgotado(String codigo) {
        excecao = null;
        cupom = new Cupom(codigo, 50, 50);
        when(cupomRepositorio.findById(codigo)).thenReturn(Optional.of(cupom));
    }

    @Quando("o usuário tenta aplicar {string} no checkout")
    public void usuario_tenta_aplicar_no_checkout(String codigo) {
        try {
            cupomServico.validarEAplicar(codigo, CPF_USUARIO, EVENTO_ID);
        } catch (Exception e) {
            excecao = e;
        }
    }

    // ----- Asserts compartilhados pelos cenários de bloqueio -----

    @Entao("o sistema deve rejeitar o cupom")
    public void sistema_deve_rejeitar_cupom() {
        assertNotNull(excecao, "Uma exceção deveria ter sido lançada.");
        assertTrue(excecao instanceof CupomException,
                "A exceção deveria ser CupomException, mas foi: " + excecao.getClass().getSimpleName());
        verify(cupomRepositorio, never()).save(any(Cupom.class));
    }

    @E("exibir a mensagem {string}")
    public void exibir_mensagem(String mensagemEsperada) {
        assertEquals(mensagemEsperada, excecao.getMessage());
    }

    // ----- Cenário: Aplicação com sucesso -----

    @Dado("que existe uma inscrição pendente do participante")
    public void existe_inscricao_pendente() {
        excecao = null;

        participante = new Pessoa();
        participante.setCpf(CPF_USUARIO);
        participante.setSaldo(500.0);
        when(pessoaRepositorio.findById(CPF_USUARIO)).thenReturn(Optional.of(participante));

        evento = new Evento();
        evento.setId(EVENTO_ID);
        evento.setCapacidade(100);

        lote = new Lote();
        lote.setId(1L);
        lote.setPreco(new BigDecimal("100.00"));
        lote.setQuantidadeDisponivel(50);
        evento.getLotes().add(lote);

        inscricaoPendente = new Inscricao(
                INSCRICAO_ID, participante, evento, lote, StatusInscricao.PENDENTE, LocalDateTime.now());
        when(inscricaoRepositorio.findById(INSCRICAO_ID)).thenReturn(Optional.of(inscricaoPendente));
        when(inscricaoRepositorio.save(any(Inscricao.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @E("o cupom {string} está disponível para uso")
    public void cupom_disponivel(String codigo) {
        cupom = new Cupom(codigo, 100, 0);
        when(cupomRepositorio.findById(codigo)).thenReturn(Optional.of(cupom));
        when(cupomRepositorio.save(any(Cupom.class))).thenAnswer(inv -> inv.getArgument(0));
        when(inscricaoRepositorio
                .existsByParticipanteCpfAndEventoIdAndCupomCodigo(CPF_USUARIO, EVENTO_ID, codigo))
                .thenReturn(false);
    }

    @Quando("o participante confirma o pagamento utilizando o cupom {string}")
    public void participante_confirma_com_cupom(String codigo) {
        try {
            inscricaoConfirmada = inscricaoServico.confirmarPagamento(
                    INSCRICAO_ID, TipoPagamento.PIX, codigo);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("a inscrição deve ser confirmada com o cupom registrado")
    public void inscricao_confirmada_com_cupom() {
        assertNull(excecao, "Não deveria ter ocorrido erro: "
                + (excecao != null ? excecao.getMessage() : ""));
        assertNotNull(inscricaoConfirmada);
        assertEquals(StatusInscricao.CONFIRMADA, inscricaoConfirmada.getStatus());
        assertEquals(cupom.getCodigo(), inscricaoConfirmada.getCupomCodigo());
    }

    @E("o uso do cupom deve ter sido contabilizado")
    public void uso_cupom_contabilizado() {
        assertEquals(1, cupom.getUsosAtuais(), "O contador de usos do cupom deveria ter incrementado.");
        verify(cupomRepositorio, times(1)).save(cupom);
    }
}