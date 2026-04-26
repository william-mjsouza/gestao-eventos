#language: pt
#utf-8

Funcionalidade: Controle de vagas no evento

  Cenario: Inscrição dentro do limite
    Dado que o evento possui 10 vagas disponíveis
    Quando um participante realiza a inscrição
    Então a inscrição deve ser confirmada
    E o saldo de vagas atualizado para 9

  Cenario: Inscrição excedendo lotação
    Dado que o evento atingiu seu limite máximo de vagas
    Quando um participante realiza a inscrição
    Então o sistema deve incluir na lista de espera