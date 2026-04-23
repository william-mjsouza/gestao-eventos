package com.gestaoeventos.apresentacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.gestaoeventos")
@EntityScan(basePackages = "com.gestaoeventos")
@EnableJpaRepositories(basePackages = "com.gestaoeventos")
public class SistemaDeGestaoDeEventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDeGestaoDeEventosApplication.class, args);
	}
}
