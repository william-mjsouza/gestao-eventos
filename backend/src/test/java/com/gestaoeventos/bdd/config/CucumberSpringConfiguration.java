package com.gestaoeventos.bdd.config;

import com.gestaoeventos.repository.EventoRepository;
import com.gestaoeventos.repository.PessoaRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {

    // Registramos todos os mocks da aplicação aqui
    @MockitoBean
    private PessoaRepository pessoaRepository;

    @MockitoBean
    private EventoRepository eventoRepository;

}