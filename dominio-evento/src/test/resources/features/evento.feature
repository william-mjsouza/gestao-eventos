#utf-8
#language: pt

Funcionalidade: Cadastro evento

  Cenario: Criar evento com sucesso
    Dado que o usuário é um organizador válido
    E preenche todos os campos obrigatórios corretamente com data futura
    E adiciona pelo menos um lote válido
    Quando ele solicita a criação do evento
    Então o evento deve ser criado e postado

  Cenario: Criar evento com data passada
    Dado que o usuário é um organizador
    Quando ele tenta criar um evento com a data de início anterior ao dia de hoje
    Então o sistema deve rejeitar a criação
    E exibir uma mensagem de erro de data inválida

  Cenario: Colisão de local físico identificada
    Dado que já existe o Evento A cadastrado no "Auditório Principal" para dia 20 das 08h às 12h
    Quando o organizador tenta criar o Evento B no "Auditório Principal" para o dia 20 das 10h às 14h
    Então o sistema deve rejeitar a criação
    E exibir mensagem informando que o local está ocupado naquele período