#utf-8
#language: pt

Funcionalidade: Gestão de Eventos Favoritos

  Cenário: Adicionar um evento aos favoritos (Toggle - Inserção)
    Dado que o usuário está navegando logado na plataforma
    Quando ele clica no ícone de favoritar em um evento
    Então o evento é salvo em sua área de "Meus Favoritos"
    E retornar a mensagem "Evento adicionado aos favoritos!"

  Cenário: Remover um evento dos favoritos (Toggle - Remoção)
    Dado que o evento já consta na lista de favoritos do usuário
    Quando ele clica novamente no ícone de favoritar
    Então o evento deve ser removido de sua lista
    E retornar a mensagem "Evento removido dos favoritos."

  Cenário: Listar os eventos favoritos de um usuário
    Dado que um usuário válido existe
    E o usuário possui eventos favoritados
    Quando o usuário solicita a lista de favoritos
    Então o sistema deve retornar a lista contendo os eventos