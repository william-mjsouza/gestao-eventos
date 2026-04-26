#utf-8
#language: pt

Funcionalidade: Limite Combinado por CPF
  
  Cenario: Tentativa de burla do limite de ingressos via carrinho
    Dado que o evento permite máximo de 2 ingressos por CPF
    E o usuário tem 1 inscrição confirmada e 1 ingresso aguardando pagamento no carrinho
    Quando ele tenta adicionar um terceiro ingresso ao carrinho
    Então o sistema deve realizar a soma
    E bloquear a ação informando limite atingido
