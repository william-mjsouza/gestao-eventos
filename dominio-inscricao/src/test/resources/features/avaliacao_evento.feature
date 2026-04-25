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

  Cenário: Geração de cashback após avaliação
    Dado que o evento está encerrado e o usuário possui inscrição confirmada
    E o saldo atual da carteira virtual do usuário é de R$ 10,00
    Quando ele envia uma nota 5 e um comentário sobre o evento
    Então o sistema deve salvar a avaliação
    E creditar automaticamente R$ 5,00 na carteira do usuário atualizando para R$ 15,00
    E gerar um aviso de cashback ao usuário

  Cenário: Tentativa de avaliar duplamente para fraudar cashback
    Dado que o usuário já enviou uma avaliação para o evento e recebeu a recompensa
    Quando ele tenta enviar uma nova avaliação para o mesmo evento alterando a nota
    Então o sistema deve rejeitar a requisição
    E informar que o usuário já avaliou este evento anteriormente