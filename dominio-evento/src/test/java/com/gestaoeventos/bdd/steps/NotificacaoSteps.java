package com.gestaoeventos.bdd.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class NotificacaoSteps {

    @Dado("que o dominio-inscricao disparou o evento de vaga alocada ao primeiro da fila")
    public void dominioInscricaoDisparouEventoDeVagaAlocada() {
        // será implementado na 2ª entrega
    }

    @Quando("o dominio-compartilhado intercepta esse evento")
    public void dominioCompartilhadoInterceptaEsseEvento() {
        // será implementado na 2ª entrega
    }

    @Então("a notificação \"Vaga Liberada - Prazo de 2 horas\" deve ser formatada e enviada instantaneamente ao usuário")
    public void aNotificacaoDeveSerFormatadaEEnviada() {
        // será implementado na 2ª entrega
    }
}