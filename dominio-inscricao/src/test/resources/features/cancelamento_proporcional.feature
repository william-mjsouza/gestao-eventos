#utf-8
#language: pt

Funcionalidade: Cancelamento com Estorno Proporcional Dinâmico

  Cenário: Cancelamento com mais de 7 dias gera estorno integral
    Dado que faltam 10 dias para o início do evento
    E o participante pagou R$ 100,00 pela inscrição
    Quando ele solicita o cancelamento pelo sistema
    Então a inscrição deve ser cancelada e a vaga liberada
    E o sistema deve creditar R$ 100,00 na carteira virtual do usuário

  Cenário: Cancelamento entre 7 dias e 48 horas gera estorno de 50%
    Dado que faltam 4 dias para o início do evento
    E o participante pagou R$ 100,00 pela inscrição
    Quando ele solicita o cancelamento pelo sistema
    Então a inscrição deve ser cancelada e a vaga liberada
    E o sistema deve creditar R$ 50,00 na carteira virtual do usuário

  Cenário: Cancelamento com menos de 48 horas não é permitido
    Dado que faltam 30 horas para o início do evento
    E o participante pagou R$ 100,00 pela inscrição
    Quando ele solicita o cancelamento pelo sistema
    Então o sistema deve rejeitar o cancelamento por prazo insuficiente
