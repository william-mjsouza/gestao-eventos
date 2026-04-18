#utf-8
#language: pt

Funcionalidade: Controle de lotes

  Cenario: Compra no lote ativo
    Dado que o Lote 1 está ativo
    E com vagas
    Quando o participante seleciona o ingresso
    Então o sistema deve aplicar o valor correspondente ao Lote 1

  Cenario: Compra em lote expirado
    Dado que a data de validade do Lote 1 expirou ontem
    Quando o usuário acessa a página do evento hoje
    Então o Lote 1 deve constar como indisponível
    E o Lote 2 deve ser oferecido