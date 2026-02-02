package com.kafka.intermediario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class SaldoDTO {

    @JsonProperty("transationalID")
    private UUID transationalID;

    @JsonProperty("pk_conta")
    private String pk_conta;

    @JsonProperty("caf")
    private String caf;

    @JsonProperty("estado")
    private String estado;

    //Dados da conta consultada
    @JsonProperty("iban")
    private String iban;

    @JsonProperty("saldo_disponivel")
    private BigDecimal saldoDisponivel;

    @JsonProperty("saldo_contabilistico")
    private BigDecimal saldoContabilistico;

    @JsonProperty("fk_cliente")
    private UUID fk_cliente;

    @Override
    public String toString(){
        return "Saldo Disponivel: " + getSaldoDisponivel() + "\nSaldo Contabilistico: " + getSaldoContabilistico() + "\nIBAN: " + getIban() + "\nFk Cliente: " + getFk_cliente();
    }

}
