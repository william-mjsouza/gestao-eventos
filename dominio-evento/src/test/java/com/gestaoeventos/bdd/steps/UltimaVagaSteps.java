package com.gestaoeventos.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class UltimaVagaSteps {

    @Dado("que resta exatamente {int} vaga no lote do evento")
    public void queRestaExatamenteVagaNoLoteDoEvento(Integer vagas) {
        // será implementado na 2ª entrega
    }

    @E("o Usuário A e o Usuário B tentam confirmar a compra ao mesmo tempo")
    public void osUsuariosTentamComprarAoMesmoTempo() {
        // será implementado na 2ª entrega
    }

    @Quando("o banco de dados processa as duas transações")
    public void oBancoDeDadosProcessaAsDuasTransacoes() {
        // será implementado na 2ª entrega
    }

    @Então("apenas uma compra deve ser efetivada")
    public void apenasUmaCompraDeveSerEfetivada() {
        // será implementado na 2ª entrega
    }

    @E("o outro usuário deve ser movido para a lista de espera")
    public void oOutroUsuarioDeveSerMovidoParaListaDeEspera() {
        // será implementado na 2ª entrega
    }
}