package com.kafka.intermediario.services;

import java.util.List;

public interface TransacaoService<T, K> {

    public List<T> transacoes();
}
