package com.gestaoeventos.bdd.steps;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = CucumberSpringConfiguration.class)
public class CucumberSpringConfiguration {
    // A classe fica vazia mesmo. O objetivo dela é apenas 
    // fornecer as anotações para o Cucumber inicializar corretamente.
}
