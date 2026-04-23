package com.gestaoeventos.bdd.config;

import com.gestaoeventos.dominio.evento.TestApplication;
import com.gestaoeventos.dominio.evento.evento.EventoRepositorio;
import com.gestaoeventos.dominio.participante.pessoa.PessoaRepositorio;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(classes = TestApplication.class)
public class CucumberSpringConfiguration {

    @MockitoBean
    private EventoRepositorio eventoRepositorio;

    @MockitoBean
    private PessoaRepositorio pessoaRepositorio;
}
