#utf-8
#language: pt

Funcionalidade: Avaliação de evento

  Cenário: Avaliação pós-evento com sucesso
    Dado que o participante compareceu ao evento
    E o status do evento consta como "Encerrado"
    Quando o usuário envia uma nota 5
    Então a avaliação deve ser salva
    E vinculada ao evento

  Cenário: Avaliação antecipada bloqueada
    Dado que o participante está inscrito em um evento marcado para amanhã
    Quando ele acessa a página do evento
    Então o botão ou área de avaliação não deve estar disponível para uso

  Cenário: Avaliação bloqueada para quem não tem inscrição confirmada
    Dado que o usuário não possui inscrição confirmada no evento
    E o evento já foi encerrado
    Quando ele tenta enviar uma avaliação
    Então o sistema deve rejeitar a avaliação
    E exibir uma mensagem informando que apenas inscritos podem avaliar
