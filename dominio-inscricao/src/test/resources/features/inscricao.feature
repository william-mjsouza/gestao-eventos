#language: pt
#utf-8


Funcionalidade: Processo de Inscrição em Eventos com Atomicidade

  Cenário: Inscrição realizada com sucesso
    Dado que o usuário "user" possui saldo de 100.0
    E o evento possui vaga no lote com preço de 60.0
    Quando o usuário conclui a inscrição
    Então o sistema deve confirmar a inscrição com sucesso
    E o saldo do usuário "user" deve ser atualizado para 40.0

  Cenário: Rollback por falha sistêmica
    Dado que o usuário "user" possui saldo de 100.0
    E o evento possui vaga no lote com preço de 60.0
    Quando ocorre um erro técnico ao registrar a inscrição final
    Então o sistema deve abortar a operação lançando um erro
    E o saldo final do banco de dados não deve ser comprometido