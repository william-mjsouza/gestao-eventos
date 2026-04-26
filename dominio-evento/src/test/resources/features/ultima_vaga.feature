#utf-8
#language: pt

Funcionalidade: Controle da Última Vaga

  Cenário: Disputa simultânea pela última vaga
    Dado que resta exatamente 1 vaga no lote do evento
    E o Usuário A e o Usuário B tentam confirmar a compra ao mesmo tempo
    Quando o banco de dados processa as duas transações
    Então apenas uma compra deve ser efetivada
    E o outro usuário deve ser movido para a lista de espera