package com.gestaoeventos.apresentacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gestaoeventos")
public class SistemaDeGestaoDeEventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaDeGestaoDeEventosApplication.class, args);
	}
}
