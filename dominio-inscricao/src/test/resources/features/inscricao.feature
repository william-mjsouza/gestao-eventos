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

  Cenário: Cancelar inscrição com antecedência
    Dado que um participante possui uma inscrição confirmada
    E o evento está marcado para daqui a 5 dias
    Quando ele solicita o cancelamento da inscrição
    Então a inscrição deve ser cancelada com sucesso
    E o saldo do participante deve ser estornado
    E a vaga deve voltar para o lote

  Cenário: Cancelar inscrição fora do prazo
    Dado que um participante possui uma inscrição confirmada
    E o evento está marcado para daqui a 2 horas
    Quando ele solicita o cancelamento da inscrição
    Então o sistema deve rejeitar o cancelamento
    E exibir uma mensagem de erro de prazo excedido