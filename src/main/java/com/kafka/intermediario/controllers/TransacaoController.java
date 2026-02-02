package com.kafka.intermediario.controllers;

import com.kafka.intermediario.services.implementacao.TransacaoServiceServiceImpl;
import com.kafka.intermediario.utils.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacao")
public class TransacaoController extends BaseController{

    @Value("${hostserver.name}")
    private String hostname;
    @Value("${hostserver.password}")
    private String password;
    @Value("${hostserver.port}")
    private String porta;

    @Autowired
    TransacaoServiceServiceImpl service;

    @GetMapping("/listar")
    public ResponseEntity<ResponseBody> listar(){
        return this.ok("Transacoes listadas com sucesso", this.service.findAll());
    }

    @GetMapping("/historico/{iban_origem}")
    public ResponseEntity<ResponseBody> consultarHistoricoCliente(@PathVariable String iban_origem){
        return this.ok("Historico Listado com sucesso", this.service.findByIbanOrigem(iban_origem));
    }

}
