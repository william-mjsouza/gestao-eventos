# Sistema de Gestão de Eventos

Sistema acadêmico para gerenciamento de eventos, inscrições e pagamentos.

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Angular

## Funcionalidades

- cadastro de evento
- cadastro de pessoa
- inscrição evento
- controle de lotes
- pagamento
- avaliações evento
- Favoritar evento
- Buscar eventos
- Carrinho
- sistema de lista de espera
- sistema de notificação
- cupom desconto
- cancelar inscrição
- Gerenciamento de status do evento
- Controle de vagas no evento
- Controle de conflito de horário
-  Limite de inscrições por pessoa

## Mapa das Histórias de Usuários

https://miro.com/app/board/uXjVG1wbDB4=/?share_link_id=839589694420

## Jira
https://projetorequisitos.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog

## Executar o projeto

```bash
.\mvnw spring-boot:run
```

## Adicionar arquivo dentro de backend\src\main\resources
```
spring.application.name=Sistema de Gestao de Eventos

spring.datasource.url=jdbc:mysql://localhost:3306/gestao_eventos?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root (USER DO MYSQL)
spring.datasource.password= SENHA DO MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
