#utf-8
#language: pt

Funcionalidade: Pagamento

  Cenário: Pagamento via PIX com saldo suficiente (Sem taxa)
    Dado que o valor total do carrinho é de R$ 100,00
    E o participante possui um saldo de R$ 150,00
    Quando ele confirmar o pagamento da inscrição via "PIX"
    Então o sistema deve debitar R$ 100,00 da carteira (atualizando o saldo para R$ 50,00)
    E a inscrição deve ser confirmada

  Cenário: Pagamento via CARTÃO com saldo suficiente (Com taxa de 5%)
    Dado que o valor total do carrinho é de R$ 100,00
    E o participante possui um saldo de R$ 150,00
    Quando ele confirmar o pagamento da inscrição via "CARTAO"
    Então o sistema deve debitar R$ 105,00 da carteira (atualizando o saldo para R$ 45,00)
    E a inscrição deve ser confirmada

  Cenário: Pagamento via CARTÃO bloqueado devido à taxa de 5%
    Dado que o valor total do carrinho é de R$ 100,00
    E o participante possui um saldo de apenas R$ 102,00
    Quando ele tentar confirmar o pagamento da inscrição via "CARTAO"
    Então o sistema deve bloquear a transação
    E exibir uma mensagem de erro informando "Saldo insuficiente para concluir a compra"

  Cenário: Pagamento genérico com saldo absolutamente insuficiente
    Dado que o valor total do carrinho é de R$ 100,00
    E o participante possui um saldo de apenas R$ 30,00
    Quando ele tentar confirmar o pagamento da inscrição via "PIX"
    Então o sistema deve bloquear a transação
    E exibir uma mensagem de erro informando "Saldo insuficiente para concluir a compra"