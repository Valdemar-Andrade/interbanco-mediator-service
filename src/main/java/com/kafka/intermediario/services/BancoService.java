package com.kafka.intermediario.services;


import java.util.List;

public interface BancoService<T, K> {
    public List<T> bancos();
}
