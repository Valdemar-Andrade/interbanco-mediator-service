package com.kafka.intermediario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class IntermediarioApplication {

    //@SpringBootApplication(scanBasePackages = {"com.", "outro.pacote.servicos"})
    public static void main(String[] args) {
        SpringApplication.run(IntermediarioApplication.class, args);
    }

}
