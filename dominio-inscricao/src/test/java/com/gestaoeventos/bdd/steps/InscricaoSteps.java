package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.evento.evento.Evento;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.evento.lote.Lote;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoException;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoRepositorio;
import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;

import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InscricaoSteps {


    private PessoaRepositorio participanteRepo;
    private EventoRepositorio eventoRepo;
    private InscricaoRepositorio inscricaoRepo;
    private InscricaoServico inscricaoServico;

    // Estado do Teste
    private Pessoa pessoa;
    private Evento evento;
    private Lote lote;
    private Exception erroCapturado;

    public InscricaoSteps() {

        participanteRepo = mock(PessoaRepositorio.class);
        eventoRepo = mock(EventoRepositorio.class);
        inscricaoRepo = mock(InscricaoRepositorio.class);

        inscricaoServico = new InscricaoServico(participanteRepo, eventoRepo, inscricaoRepo);
    }


    @Dado("que o usuário {string} possui saldo de {double}")
    public void que_o_usuario_possui_saldo_de(String cpf, Double saldoInicial) {
        pessoa = new Pessoa();
        pessoa.setCpf(cpf);
        pessoa.setSaldo(saldoInicial);

        when(participanteRepo.buscarPorCpf(cpf)).thenReturn(Optional.of(pessoa));
    }

    @Dado("o evento possui vaga no lote com preço de {double}")
    public void o_evento_possui_vaga_no_lote_com_preco_de(Double preco) {
        lote = new Lote();
        lote.setId(1L);
        lote.setPreco(BigDecimal.valueOf(preco));
        lote.setQuantidadeDisponivel(10); // Lote com vagas

        evento = new Evento();
        evento.setId(100L);
        evento.getLotes().add(lote);


        when(eventoRepo.buscarPorId(100L)).thenReturn(Optional.of(evento));


        when(inscricaoRepo.jaInscrito(anyString(), anyLong())).thenReturn(false);
    }


    @Quando("o usuário conclui a inscrição")
    public void o_usuario_conclui_a_inscricao() {
        try {
            inscricaoServico.realizarInscricao(pessoa.getCpf(), evento.getId(), lote.getId());
        } catch (Exception e) {
            this.erroCapturado = e;
        }
    }

    @Entao("o sistema deve confirmar a inscrição com sucesso")
    public void o_sistema_deve_confirmar_a_inscricao_com_sucesso() {
        Assertions.assertNull(erroCapturado, "Não deveria ter lançado exceção");

        // Verifica se o método de salvar no banco foi chamado exatamente 1 vez
        verify(inscricaoRepo, times(1)).salvar(any());
    }

    @Entao("o saldo do usuário {string} deve ser atualizado para {double}")
    public void o_saldo_do_usuario_deve_ser_atualizado_para(String cpf, Double saldoEsperado) {
        Assertions.assertEquals(saldoEsperado, pessoa.getSaldo());


        verify(participanteRepo, times(1)).atualizarSaldo(pessoa);
    }



    @Quando("ocorre um erro técnico ao registrar a inscrição final")
    public void ocorre_um_erro_tecnico_ao_registrar_a_inscricao_final() {
        // FORÇANDO A FALHA: Quando tentar salvar, lança erro de banco de dados
        doThrow(new RuntimeException("Banco de dados fora do ar"))
                .when(inscricaoRepo).salvar(any());

        try {
            inscricaoServico.realizarInscricao(pessoa.getCpf(), evento.getId(), lote.getId());
        } catch (Exception e) {
            this.erroCapturado = e;
        }
    }

    @Entao("o sistema deve abortar a operação lançando um erro")
    public void o_sistema_deve_abortar_a_operacao_lancando_um_erro() {
        Assertions.assertNotNull(erroCapturado, "Uma exceção deveria ter sido lançada");
        Assertions.assertTrue(erroCapturado instanceof InscricaoException);
        Assertions.assertEquals("Falha ao gerar ingresso. Operação revertida.", erroCapturado.getMessage());
    }

    @Entao("o saldo final do banco de dados não deve ser comprometido")
    public void o_saldo_final_do_banco_de_dados_nao_deve_ser_comprometido() {

        verify(inscricaoRepo, times(1)).salvar(any());
    }
}