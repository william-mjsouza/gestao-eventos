#utf-8
#language: pt

Funcionalidade: Validacao de idade na inscricao do evento

  Cenário: Inscrição permitida para participante com idade exata na data do evento
    Dado que o evento exige idade mínima de 18 anos
    E a data de início do evento está marcada para "31/12/2026"
    E o participante informou sua data de nascimento como "31/12/2008"
    Quando ele tenta realizar a inscrição
    Então o sistema deve permitir a inscrição com sucesso

  Cenário: Inscrição permitida para participante com idade superior na data do evento
    Dado que o evento exige idade mínima de 18 anos
    E a data de início do evento está marcada para "31/12/2026"
    E o participante informou sua data de nascimento como "31/12/2002"
    Quando ele tenta realizar a inscrição
    Então o sistema deve permitir a inscrição com sucesso

  Cenário: Inscrição bloqueada para participante menor de idade na data do evento
    Dado que o evento exige idade mínima de 18 anos
    E a data de início do evento está marcada para "31/12/2026"
    E o participante informou sua data de nascimento como "01/01/2009"
    Quando ele tenta realizar a inscrição
    Então o sistema deve bloquear a inscrição
    E exibir uma mensagem de erro alertando sobre a idade insuficiente