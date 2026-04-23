#utf-8
#language: pt

Funcionalidade: Cadastro participante
  Cenario: Cadastro valido
    Dado que o usuário preenche todos os dados obrigatórios corretamente com CPF e e-mail únicos
    Quando ele submete o formulário
    Entao o sistema deve criar a conta

  Cenario: Cadastro com cpf ja existente
    Dado que já existe um participante registrado com o CPF "527.183.640-20"
    Quando um novo usuário tenta se cadastrar utilizando o mesmo CPF
    Entao o sistema deve rejeitar o cadastro alertando que o cpf já está em uso

  Cenario:  Cadastro com email ja existente
    Dado que já existe um participante registrado com o email "teste@gmail.com"
    Quando um novo usuário tenta se cadastrar utilizando o mesmo email
    Entao o sistema deve rejeitar o cadastro alertando que o email já está em uso