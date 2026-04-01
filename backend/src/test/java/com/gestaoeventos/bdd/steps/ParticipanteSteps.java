package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.entity.Participante;
import com.gestaoeventos.exception.ParticipanteException;
import com.gestaoeventos.repository.ParticipanteRepository;
import com.gestaoeventos.service.ParticipanteService;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest
public class ParticipanteSteps {

    @Autowired
    private ParticipanteService participanteService;

    @MockitoBean
    private ParticipanteRepository participanteRepository;

    private Participante participante;
    private Exception excecao;


    @Dado("que o usuário preenche todos os dados obrigatórios corretamente com CPF e e-mail únicos")
    public void preencher_dados_validos() {
        participante = new Participante();
        participante.setNome("Teste");
        participante.setSenha("123456");
        participante.setEmail("Teste@gmail.com");
        participante.setCpf("52718364020");
        participante.setSaldo(0.0);

        // simular o banco
        when(participanteRepository.existsById(anyString())).thenReturn(false);
        when(participanteRepository.existsByEmail(anyString())).thenReturn(false);
    }

    @Quando("ele submete o formulário")
    public void enviar_cadastro(){
        try{
            participanteService.salvar(participante);
        }catch (Exception e){
            excecao = e;
        }
    }

    @Entao("o sistema deve criar a conta")
    public void verifica_conta_criada(){
        assertNull(excecao, "Não deveria ter ocorrido erro no cadastro válido");
        verify(participanteRepository, times(1)).save(any(Participante.class));
    }


    @Dado("que já existe um participante registrado com o CPF {string}")
    public void verifica_cpf_jaexistente(String cpfIformado){
        when(participanteRepository.existsById(cpfIformado)).thenReturn(true);

        participante = new Participante();
        participante.setCpf(cpfIformado);
        participante.setEmail("novo@gmail.com");
        participante.setNome("Usuário Teste");
        participante.setSenha("123");
    }

    @Quando("um novo usuário tenta se cadastrar utilizando o mesmo CPF")
    public void tenta_cadastrar_utilizando_o_mesmo_cpf(){
        enviar_cadastro();
    }


    @Dado("que já existe um participante registrado com o email {string}")
    public void emailJaExistente(String emailInformado) {
        when(participanteRepository.existsByEmail(emailInformado)).thenReturn(true);

        participante = new Participante();
        participante.setCpf("04592186033");
        participante.setEmail(emailInformado);
        participante.setNome("Usuário Teste 2");
        participante.setSenha("123");
    }

    @Quando("um novo usuário tenta se cadastrar utilizando o mesmo email")
    public void tentarCadastrarMesmoEmail() {
        enviar_cadastro();
    }

    // serve para os dois cenários
    @Entao("o sistema deve rejeitar o cadastro alertando que o {word} já está em uso")
    public void verificar_rejeicao_de_cadastro(String campo) {
        assertNotNull(excecao, "O sistema deveria ter lançado um erro, mas não lançou.");
        assertTrue(excecao instanceof ParticipanteException, "O erro deveria ser um ParticipanteException");

        String mensagemErro = excecao.getMessage().toLowerCase();
        assertTrue(mensagemErro.contains(campo.toLowerCase()),
                "A mensagem de erro deveria mencionar o campo: " + campo);

        verify(participanteRepository, never()).save(any(Participante.class));
    }
}