package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.inscricao.cupom.Cupom;
import com.gestaoeventos.dominio.inscricao.cupom.CupomRepository;
import com.gestaoeventos.dominio.inscricao.cupom.CupomServico;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.Optional;

public class CupomSteps {


    private CupomRepository cupomRepository = Mockito.mock(CupomRepository.class);
    private InscricaoRepositorio inscricaoRepositorio = Mockito.mock(InscricaoRepositorio.class);


    private CupomServico cupomServico = new CupomServico(cupomRepository, inscricaoRepositorio);


    private String codigoCupom;
    private int limiteUsos;
    private int usosAtuais;
    private Cupom cupom;
    private String cpfUsuario = "123.456.789-00";
    private Long eventoId = 1L;
    private Exception excecaoLancada;



    @Dado("que o cupom {string} possui limite de {int} usos")
    public void que_o_cupom_possui_limite_de_usos(String codigo, Integer limite) {
        this.codigoCupom = codigo;
        this.limiteUsos = limite;
    }

    @Dado("o cupom foi utilizado {int} vezes até o momento")
    public void o_cupom_foi_utilizado_vezes_ate_o_momento(Integer usos) {
        this.usosAtuais = usos;
        this.cupom = new Cupom(this.codigoCupom, this.limiteUsos, this.usosAtuais);
    }

    @Dado("o usuário ainda não utilizou este cupom no evento")
    public void o_usuario_ainda_nao_utilizou_este_cupom_no_evento() {
        Mockito.when(inscricaoRepositorio.existeUsoDeCupomPorCpfEEvento(cpfUsuario, codigoCupom, eventoId))
                .thenReturn(false);
    }

    @Quando("o usuário tenta aplicar {string} no carrinho")
    public void o_usuario_tenta_aplicar_no_carrinho(String codigo) {
        Mockito.when(cupomRepository.buscarPorCodigo(codigo)).thenReturn(Optional.of(cupom));
        try {
            cupomServico.validarEAplicar(codigo, cpfUsuario, eventoId);
        } catch (Exception e) {
            this.excecaoLancada = e;
        }
    }

    @Então("o sistema deve aceitar o cupom")
    public void o_sistema_deve_aceitar_o_cupom() {
        Assertions.assertNull(excecaoLancada, "Não deveria ter lançado erro: " + (excecaoLancada != null ? excecaoLancada.getMessage() : ""));
    }

    @Então("registrar o uso do cupom com sucesso")
    public void registrar_o_uso_do_cupom_com_sucesso() {
        Mockito.verify(cupomRepository, Mockito.times(1)).atualizarUso(cupom);
    }



    @Dado("que o cupom {string} possui a regra de {int} uso por CPF")
    public void que_o_cupom_possui_a_regra_de_uso_por_cpf(String codigo, Integer limiteCpf) {
        this.codigoCupom = codigo;
        // Limite geral alto para não interferir no teste de CPF
        this.cupom = new Cupom(codigo, 1000, 0);
    }

    @Dado("o usuário já realizou uma compra anterior utilizando este cupom")
    public void o_usuario_ja_realizou_uma_compra_anterior_utilizando_este_cupom() {
        Mockito.when(inscricaoRepositorio.existeUsoDeCupomPorCpfEEvento(cpfUsuario, codigoCupom, eventoId))
                .thenReturn(true);
    }

    @Quando("o usuário tenta aplicar {string} novamente em um novo carrinho")
    public void o_usuario_tenta_aplicar_novamente_em_um_novo_carrinho(String codigo) {
        this.o_usuario_tenta_aplicar_no_carrinho(codigo);
    }

    @Então("o sistema deve rejeitar o cupom")
    public void o_sistema_deve_rejeitar_o_cupom() {
        Assertions.assertNotNull(excecaoLancada, "O sistema deveria ter rejeitado o cupom!");
    }

    @Então("exibir a mensagem {string}")
    public void exibir_a_mensagem(String mensagemEsperada) {
        Assertions.assertEquals(mensagemEsperada, excecaoLancada.getMessage());
    }



    @Dado("o cupom já foi utilizado {int} vezes até o momento")
    public void o_cupom_ja_foi_utilizado_vezes_ate_o_momento(Integer usos) {
        this.usosAtuais = usos;
        this.cupom = new Cupom(this.codigoCupom, this.limiteUsos, this.usosAtuais);
    }
}