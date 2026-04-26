#utf-8
#language: pt

Funcionalidade: Fila de Espera

  Cenário: Repasse de vaga por expiração de tempo limite
    Dado que a vaga foi alocada no carrinho do Usuário A (primeiro da fila)
    E o tempo limite para pagamento é de 2 horas
    Quando o tempo limite é atingido sem que o Usuário A finalize a compra
    Então o sistema deve remover o ingresso do carrinho do Usuário A
    E adicionar o ingresso no carrinho do Usuário B (segundo da fila)