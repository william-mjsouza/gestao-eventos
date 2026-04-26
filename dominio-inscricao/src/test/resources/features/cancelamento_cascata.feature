#utf-8
#language: pt

Funcionalidade: Cancelamento em Cascata de Eventos

  Cenário: Cancelamento em cascata cancela todas as inscrições ativas
    Dado que o evento possui 50 participantes confirmados
    Quando o organizador altera o status do evento para "Cancelado"
    Então o status de todas as 50 inscrições deve passar para "Cancelada pelo Organizador"
    E o valor pago deve ser devolvido integralmente para as 50 carteiras virtuais
    E notificações de cancelamento devem ser disparadas

  Cenário: Inscrições pendentes também são canceladas em cascata
    Dado que o evento possui participantes com inscrições pendentes e confirmadas
    Quando o organizador altera o status do evento para "Cancelado"
    Então todas as inscrições confirmadas devem ter seu saldo estornado
    E todas as inscrições pendentes devem ser canceladas sem estorno
