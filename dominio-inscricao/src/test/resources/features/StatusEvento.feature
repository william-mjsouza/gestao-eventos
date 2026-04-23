#utf-8
#language: pt

Funcionalidade: Controle de Status do Evento

  Cenário: Alterar status para ativo
    Dado que o evento possui todos os dados válidos
    Quando o organizador publica o evento
    Então o evento deve ser publicado
    E ficar disponível para inscrições

  Cenário: Impedir inscrição em evento cancelado
    Dado que o status do evento consta como cancelado
    Quando um usuário tenta se inscrever ao evento
    Então o sistema deve impedir a ação
    E informar que o evento foi cancelado