package com.kafka.intermediario.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "banco")
public class Banco {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "pk_banco", updatable = false, nullable = false)
    private UUID pk_banco;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "caf")
    private String caf;

}
