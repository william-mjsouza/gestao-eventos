#utf-8
#language: pt

Funcionalidade: Validação Combinatória de Cupons de Desconto

  Cenário: Aplicação de cupom válido
    Dado que o cupom "PROMO10" possui limite de 100 usos
    E o cupom foi utilizado 50 vezes até o momento
    E o usuário ainda não utilizou este cupom no evento
    Quando o usuário tenta aplicar "PROMO10" no carrinho
    Então o sistema deve aceitar o cupom
    E registrar o uso do cupom com sucesso

  Cenário: Uso único por CPF bloqueado
    Dado que o cupom "BOASVINDAS" possui a regra de 1 uso por CPF
    E o usuário já realizou uma compra anterior utilizando este cupom
    Quando o usuário tenta aplicar "BOASVINDAS" novamente em um novo carrinho
    Então o sistema deve rejeitar o cupom
    E exibir a mensagem "Cupom já utilizado por este usuário"

  Cenário: Tentativa de uso de cupom esgotado
    Dado que o cupom "LIMITADO50" possui limite de 50 usos
    E o cupom já foi utilizado 50 vezes até o momento
    Quando o usuário tenta aplicar "LIMITADO50" no carrinho
    Então o sistema deve rejeitar o cupom
    E exibir a mensagem "Cupom esgotado"