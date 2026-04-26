#utf-8
#language: pt

Funcionalidade: Validação combinatória de cupons de desconto

  Cenário: Uso único por CPF bloqueado
    Dado que o cupom "BOASVINDAS" possui a regra de 1 uso por CPF
    E o usuário já realizou uma compra anterior utilizando este cupom
    Quando o usuário tenta aplicar "BOASVINDAS" novamente em um novo carrinho
    Então o sistema deve rejeitar o cupom
    E exibir a mensagem "Cupom já utilizado por este usuário"

  Cenário: Cupom esgotado bloqueado
    Dado que o cupom "PROMO50" atingiu o limite total de usos
    Quando o usuário tenta aplicar "PROMO50" no checkout
    Então o sistema deve rejeitar o cupom
    E exibir a mensagem "Cupom esgotado."

  Cenário: Cupom aplicado com sucesso na confirmação do pagamento
    Dado que existe uma inscrição pendente do participante
    E o cupom "DESCONTO10" está disponível para uso
    Quando o participante confirma o pagamento utilizando o cupom "DESCONTO10"
    Então a inscrição deve ser confirmada com o cupom registrado
    E o uso do cupom deve ter sido contabilizado