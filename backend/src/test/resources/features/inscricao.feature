#utf-8
#language: pt

Funcionalidade: Inscrição em evento

  Cenário: Inscrição registrada
    Dado que o evento está ativo
    E possui vagas
    Quando o participante realiza o pagamento
    Então o sistema deve inscrever o participante

  Cenário: Inscrição duplicada bloqueada
    Dado que o usuário já está inscrito no evento
    Quando ele tenta iniciar uma nova inscrição para o mesmo evento
    Então o sistema deve alertar que ele já possui participação