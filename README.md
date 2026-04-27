# Sistema de Gestão de Eventos

Sistema acadêmico para gerenciamento integral de eventos, abrangendo a criação e administração por organizadores, o ciclo completo de inscrições e o processamento de pagamentos pelos participantes.

---

## 1. Visão Geral

A solução cobre todo o fluxo de um evento: desde a publicação pelo organizador, passando pela venda em lotes, controle de vagas e fila de espera, até o pós-evento (avaliações e cashback). É construída sobre uma arquitetura multi-módulo Maven, com banco relacional e API REST consumida por um frontend web.

---

## 2. Arquitetura e Padrões de Design

### 2.1. Fundamentos arquiteturais

- **Domain-Driven Design (DDD)** — modelagem estratégica (subdomínios e bounded contexts), tática (agregados, entidades, value objects, serviços de domínio, repositórios) e operacional (eventos de domínio).
- **Arquitetura Limpa / Hexagonal** — separação rigorosa entre domínio, aplicação, infraestrutura e apresentação. As anotações JPA estão isoladas no módulo `infraestrutura`, mantendo o domínio livre de dependências de framework de persistência.
- **Modularização Maven** — cada bounded context corresponde a um módulo Maven, refletindo a estrutura lógica do domínio.
- **Padrões GoF** aplicados ao longo do código: Iterator, Decorator, Observer, Proxy, Strategy e Template Method.

### 2.2. Estrutura de módulos

```
gestao-eventos/
├── dominio-compartilhado/   ← Enums e exceção base
├── dominio-participante/    ← Agregado Pessoa e carteira virtual
├── dominio-evento/          ← Agregados Evento, Lote e Relatório
├── dominio-inscricao/       ← Agregados Inscrição, Lista de Espera, Carrinho, Cupom, Avaliação e Favorito
├── aplicacao/               ← DTOs e serviços de aplicação (use cases)
├── infraestrutura/          ← Implementações JPA dos repositórios
└── apresentacao-backend/    ← Controllers REST e GlobalExceptionHandler
```

### 2.3. Bounded Contexts

Conforme o mapa de contextos (`gestao-eventos.cml`), o sistema é segregado em três contextos delimitados:

| Contexto | Responsabilidade |
|---|---|
| **Evento** | Criação e gestão de eventos, definição de lotes de ingressos e geração de relatórios operacionais (ocupação e receita). |
| **Inscrição** | Ciclo de vida da inscrição: reserva, pagamento, cancelamento, lista de espera, carrinho, cupons, avaliação pós-evento e favoritos. |
| **Participante** | Cadastro de usuários (participantes e organizadores), autenticação e manutenção do saldo da carteira virtual. |

---

## 3. Regras de Negócio

O comportamento do sistema é validado por cenários BDD (Behavior-Driven Development) escritos em Gherkin e executados com Cucumber.

### 3.1. Gestão de eventos e lotes

- **Publicação e prevenção de conflitos.** Organizadores publicam eventos com capacidade e lotes definidos. O sistema impede a criação se houver sobreposição de horário no mesmo local físico.
- **Atualização dinâmica de preço.** O valor dos ingressos é definido por lotes. Caso um lote esgote durante a navegação ou checkout, o sistema notifica a virada de lote e recalcula o valor do carrinho dinamicamente.

### 3.2. Controle de ocupação e fila de espera

- **Lotação máxima.** Ao atingir o limite de vagas, novas tentativas de inscrição são direcionadas automaticamente para a lista de espera.
- **Alocação com tempo limite (TTL).** Quando uma vaga é liberada por cancelamento, ela é temporariamente alocada ao primeiro usuário da fila, que tem 2 horas para concluir o pagamento. Se o prazo expirar, o ingresso é transferido para o próximo da fila.

### 3.3. Validações e restrições de inscrição

- **Conflito de agenda do participante.** A inscrição é bloqueada se o horário do evento conflitar com outro evento no qual o participante já esteja confirmado.
- **Idade mínima.** A inscrição é bloqueada automaticamente se o participante não atender à idade mínima exigida na data de realização do evento.
- **Limite por CPF.** O sistema soma inscrições pendentes (no carrinho) e confirmadas para impor o limite máximo de ingressos por CPF.

### 3.4. Pagamentos e cupons

- **Carteira virtual.** Os pagamentos descontam o saldo da carteira do usuário. PIX não tem taxa; cartão tem taxa de 5%.
- **Segurança transacional.** Transações com saldo insuficiente são bloqueadas. Em caso de falha na geração do ingresso, ocorre rollback completo (restauração de saldo e vaga).
- **Validação de cupons.** Cupons são validados no checkout e rejeitados se esgotados ou se violarem a regra de uso único por CPF.

### 3.5. Cancelamentos e estornos

- **Cancelamento pelo participante.** Permitido até 48h antes do evento, com estorno integral do valor do lote. Cancelamentos com menos de 48 horas de antecedência são bloqueados pelo sistema.
- **Cancelamento em cascata.** Se o organizador altera o status do evento para "Cancelado", o sistema cancela todas as inscrições pendentes e confirmadas, processa o estorno integral aos confirmados e dispara notificações.

### 3.6. Engajamento (avaliações, cashback e favoritos)

- **Processo de avaliação.** Disponível apenas para participantes confirmados e somente após o encerramento do evento.
- **Cashback.** A submissão de uma avaliação válida credita R$ 5,00 na carteira virtual.
- **Prevenção de fraudes.** Avaliações múltiplas para o mesmo evento são bloqueadas.
- **Favoritos.** Usuários autenticados gerenciam uma lista pessoal através de uma funcionalidade toggle.

### 3.7. Relatórios

- **Relatório de Ocupação e Receita.** Organizadores extraem relatórios detalhados, somando vendas efetivas e calculando a taxa de ocupação real. Inscrições canceladas são excluídas do faturamento líquido.

---

## 4. Funcionalidades

- Cadastro de evento e de pessoa
- Inscrição em evento e controle de lotes
- Pagamento via carteira virtual (PIX/cartão) e cupons de desconto
- Carrinho de compras com TTL
- Lista de espera com fila e prazo de pagamento
- Cancelamento de inscrição e cancelamento em cascata pelo organizador
- Sistema de notificação
- Avaliações de evento com cashback
- Favoritar eventos
- Busca de eventos
- Gerenciamento de status do evento
- Controle de vagas e de conflito de horário
- Limite de inscrições por CPF
- Relatórios de ocupação e receita

---

## 5. Stack Tecnológico

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.5 |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | MySQL 8.0+ (produção) / H2 (testes) |
| Build | Maven |
| Testes BDD | Cucumber 7 + JUnit 5 |
| Frontend (planejado) | Angular |

---

## 6. Como Executar

### 6.1. Compilar o projeto

Na raiz do projeto:

```bash
mvn clean install -DskipTests
```

### 6.2. Subir o backend

Dentro do módulo `apresentacao-backend`:

```bash
mvn spring-boot:run
```

### 6.3. Configuração do banco de dados

Crie o arquivo `apresentacao-backend/src/main/resources/application.properties` apontando para sua instância MySQL local:

```properties
spring.application.name=Sistema de Gestao de Eventos

spring.datasource.url=jdbc:mysql://localhost:3306/gestao_eventos?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=SEU_USUARIO_MYSQL
spring.datasource.password=SUA_SENHA_MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 6.4. Executar os testes

```bash
# Todos os testes
mvn test

# Testes de um domínio específico
mvn test -pl dominio-inscricao -Dtest=CucumberRunnerTest
```

---

## 7. Links do Projeto

- **Mapa das Histórias de Usuários (Miro):** https://miro.com/app/board/uXjVG1wbDB4=/?share_link_id=839589694420
- **Funcionalidades:** https://docs.google.com/document/d/1PZuCj7tF4efCE54N9hjjhOQTANeV_AwX9MOFAhRuOtA/edit?tab=t.0#heading=h.ib893ooses5u
- Slides entrega 1: https://canva.link/hy5f6r6vbo94bnl
- Figma: https://www.figma.com/make/7gvoFtC0Ui3Y6BVR0jFP68/Prototipo-de-baixa-fidelidade?p=f&t=yqTe9BdmtnB5a9bc-0&fullscreen=1
