#utf-8
#language: pt

Funcionalidade: Geração de Relatórios de Ocupação e Receita

  Cenário: Cálculo correto da receita com diferentes lotes e cancelamentos
    Dado que um evento teve 10 vendas no Lote 1 de R$ 50,00 e 10 vendas no Lote 2 de R$ 100,00
    E ocorreram 2 cancelamentos do Lote 2
    Quando o organizador solicita o relatório financeiro
    Então o sistema deve agregar os dados corretamente
    E exibir a receita total líquida de R$ 1300,00 e ocupação de 18 vagas ativas