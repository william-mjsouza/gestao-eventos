#utf-8
#language: pt

Funcionalidade: Notificações Transacionais

  Cenário: Envio imediato de aviso da lista de espera
    Dado que o dominio-inscricao disparou o evento de vaga alocada ao primeiro da fila
    Quando o dominio-compartilhado intercepta esse evento
    Então a notificação "Vaga Liberada - Prazo de 2 horas" deve ser formatada e enviada instantaneamente ao usuário