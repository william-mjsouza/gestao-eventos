#utf-8
#language: pt

Funcionalidade: Pagamento

  Cenário: Pagamento com saldo suficiente
    Dado que o valor total do carrinho é de R$ 100,00
    E o participante possui um saldo de R$ 150,00
    Quando ele confirmar o pagamento da inscrição
    Então o sistema deve debitar R$ 100,00 da carteira (atualizando o saldo para R$ 50,00)
    E a inscrição deve ser confirmada

  Cenário: Pagamento com saldo insuficiente
    Dado que o valor total do carrinho é de R$ 100,00
    E o participante possui um saldo de apenas R$ 30,00
    Quando ele tentar confirmar o pagamento da inscrição
    Então o sistema deve bloquear a transação
    E exibir uma mensagem de erro informando "Saldo insuficiente para concluir a compra"