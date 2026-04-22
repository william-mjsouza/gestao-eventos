#utf-8
#language: pt

Funcionalidade: Controle de vagas no evento

  Cenário: Inscrição dentro do limite
    Dado que o evento possui 10 vagas disponíveis
    Quando um participante realiza a inscrição para 1 vaga
    Então a inscrição deve ser registrada com sucesso
    E o saldo de vagas atualizado para 9

  Cenário: Inscrição excedendo lotação
    Dado que o evento atingiu seu limite máximo de vagas
    Quando um usuário tenta se inscrever
    Então o sistema deve incluir na lista de espera
