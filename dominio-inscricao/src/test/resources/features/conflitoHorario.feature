#utf-8
#language: pt

Funcionalidade: Controle de Inscrição

  Cenário: Bloqueio por sobreposição parcial de horário
    Dado que o participante está inscrito em um evento das "14:00" às "16:00" no dia "2026-10-10"
    Quando ele tenta se inscrever em um novo evento das "15:30" às "17:30" no mesmo dia
    Então o sistema deve bloquear a inscrição por choque de agenda