#utf-8
#language: pt

Funcionalidade: Dinâmica de Lotes

  Cenario: Atualização de preço no carrinho por virada de lote
    Dado que o usuário tem 1 ingresso do Lote 1 no carrinho com valor de R$ 50,00
    E o Lote 1 esgota as vagas no sistema enquanto ele navega
    Quando o usuário avança para a tela final de pagamento
    Então o sistema deve informar que o lote virou
    E atualizar o valor para o Lote 2 com valor de R$ 70,00 antes de cobrar