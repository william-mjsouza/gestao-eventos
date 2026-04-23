#utf-8
#language: pt

Funcionalidade: Sistema de lista de espera

  Cenário: Pagamento da vaga de espera dentro do prazo
    Dado que uma vaga foi liberada no evento lotado
    E o ingresso foi adicionado automaticamente ao carrinho do primeiro usuário da lista de espera
    E o usuário recebeu a notificação de aviso
    Quando ele acessa o carrinho
    E finaliza o pagamento dentro do tempo limite
    Então a inscrição da lista de espera deve ser confirmada
    E o usuário deve ser removido da lista de espera

  Cenário: Expiração do prazo da vaga no carrinho
    Dado que o ingresso foi alocado no carrinho do primeiro usuário da lista de espera
    E o tempo limite para pagamento era de 2 horas
    Quando não realiza o pagamento dentro do tempo limite
    Então o sistema deve remover o ingresso do carrinho desse usuário
    E adicionar o ingresso no carrinho do próximo participante da fila
