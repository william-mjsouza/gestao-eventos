package com.gestaoeventos.dominio.compartilhado;

public enum StatusListaEspera {
    AGUARDANDO, // Na fila, esperando surgir vaga
    CARRINHO,   // Vaga liberada, aguardando pagamento (dentro do TTL)
    CONFIRMADO, // Pagamento realizado com sucesso
    EXPIRADO,   // Não pagou no prazo de 2 horas
    CANCELADO
}