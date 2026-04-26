package com.gestaoeventos.bdd.steps;

import com.gestaoeventos.dominio.participante.pessoa.ParticipanteException;
import com.gestaoeventos.dominio.participante.pessoa.Pessoa;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.PessoaServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PessoaSteps {

    @Autowired
    private PessoaServico pessoaServico;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    private Pessoa pessoa;
    private Exception excecao;

    @Dado("que o usuário preenche todos os dados obrigatórios corretamente com CPF e e-mail únicos")
    public void preencher_dados_validos() {
        pessoa = new Pessoa();
        pessoa.setNome("Teste");
        pessoa.setSenha("123456");
        pessoa.setEmail("Teste@gmail.com");
        pessoa.setCpf("52718364020");
        pessoa.setOrganizador(false);
        pessoa.setSaldo(0.0);
        pessoa.setDataNascimento(LocalDate.of(2000, 1, 1));


        when(pessoaRepositorio.existsById(anyString())).thenReturn(false);
        when(pessoaRepositorio.existsByEmail(anyString())).thenReturn(false);
    }

    @Quando("ele submete o formulário")
    public void enviar_cadastro() {
        try {
            pessoaServico.salvar(pessoa);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Entao("o sistema deve criar a conta")
    public void verifica_conta_criada() {
        assertNull(excecao, "Não deveria ter ocorrido erro no cadastro válido");
        verify(pessoaRepositorio, times(1)).save(any(Pessoa.class));
    }

    @Dado("que já existe um participante registrado com o CPF {string}")
    public void verifica_cpf_jaexistente(String cpfInformado) {
        when(pessoaRepositorio.existsById(cpfInformado)).thenReturn(true);

        pessoa = new Pessoa();
        pessoa.setCpf(cpfInformado);
        pessoa.setEmail("novo@gmail.com");
        pessoa.setNome("Usuário Teste");
        pessoa.setSenha("123");
        pessoa.setOrganizador(false);
        pessoa.setSaldo(0.0);
    }

    @Quando("um novo usuário tenta se cadastrar utilizando o mesmo CPF")
    public void tenta_cadastrar_utilizando_o_mesmo_cpf() {
        enviar_cadastro();
    }

    @Dado("que já existe um participante registrado com o email {string}")
    public void emailJaExistente(String emailInformado) {
        when(pessoaRepositorio.existsByEmail(emailInformado)).thenReturn(true);

        pessoa = new Pessoa();
        pessoa.setCpf("04592186033");
        pessoa.setEmail(emailInformado);
        pessoa.setNome("Usuário Teste 2");
        pessoa.setSenha("123");
        pessoa.setOrganizador(false);
        pessoa.setSaldo(0.0);
        pessoa.setDataNascimento(LocalDate.of(2000, 1, 1));
    }

    @Quando("um novo usuário tenta se cadastrar utilizando o mesmo email")
    public void tentarCadastrarMesmoEmail() {
        enviar_cadastro();
    }

    @Entao("o sistema deve rejeitar o cadastro alertando que o {word} já está em uso")
    public void verificar_rejeicao_de_cadastro(String campo) {
        assertNotNull(excecao, "O sistema deveria ter lançado um erro, mas não lançou.");
        assertTrue(excecao instanceof ParticipanteException, "O erro deveria ser um ParticipanteException");

        String mensagemErro = excecao.getMessage().toLowerCase();
        assertTrue(mensagemErro.contains(campo.toLowerCase()),
                "A mensagem de erro deveria mencionar o campo: " + campo);

        verify(pessoaRepositorio, never()).save(any(Pessoa.class));
    }
}
