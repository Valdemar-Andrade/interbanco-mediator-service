package com.kafka.intermediario.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.intermediario.dto.SaldoDTO;
import com.kafka.intermediario.dto.TransacaoDTO;
import com.kafka.intermediario.entities.Banco;
import com.kafka.intermediario.entities.CodigoEncriptacao;
import com.kafka.intermediario.services.implementacao.CodigoEncriptacaoServiceImpl;
import com.kafka.intermediario.utils.ArrayTransacaoManipulator;
import com.kafka.intermediario.utils.EncryptionUtil;
import com.kafka.intermediario.utils.ResponseBody;
import com.kafka.intermediario.services.implementacao.BancoServiceImpl;
import com.kafka.intermediario.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.UUID;

@RestController
@RequestMapping("/banco")
public class BancoController extends BaseController {

    @Value("${hostserver.name}")
    private String hostname;
    @Value("${hostserver.password}")
    private String password;
    @Value("${hostserver.port}")
    private String porta;

    @Autowired
    BancoServiceImpl service;

    @Autowired
    private CodigoEncriptacaoServiceImpl codigoEncriptacaoService;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public BancoController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String base(){
        return "Eu sou o Intermediario";
    }

    @PostMapping("/criar")
    public ResponseEntity<ResponseBody> cadastrarBanco(@RequestBody Banco banco){
        return this.created("Banco criado com sucesso", this.service.criar(banco));
    }

    @GetMapping("/listar")
    public ResponseEntity<ResponseBody> listar(){
        return this.ok("Bancos listados com sucesso", this.service.findAll());
    }

    @GetMapping("/saldo")
    public String consultarSaldoConta(@RequestBody SaldoDTO saldoDTO) throws JsonProcessingException {

        saldoDTO.setTransationalID(UUIDGenerator.gerarUUID());

        ArrayTransacaoManipulator.arrayTransacoesConsulta.add(saldoDTO);

        String transacaoJson = objectMapper.writeValueAsString(saldoDTO);

        //encriptar a mensagem antes de ser enviada
        String chaveString = codigoEncriptacaoService.getChaveFromDB().getCodigo();
        SecretKey secretKey = EncryptionUtil.stringToKey(chaveString);

        String transacaoEncriptada;

        try {

            transacaoEncriptada = EncryptionUtil.encrypt(transacaoJson, secretKey);
            kafkaTemplate.send("interban", transacaoEncriptada);

        } catch (Exception e) {

        }

        return "Verifique a saida no terminal!";
    }

    /*
    @GetMapping("/gerarChave")
    public ResponseEntity<ResponseBody> gerarChave() throws Exception {

        //salvar a chave convertida para string
        CodigoEncriptacao c = new CodigoEncriptacao();
        c.setCodigo("+bmZFE59mX42MTV3MjXrSQ==");

        return this.created("Chave gerada com sucesso", this.codigoEncriptacaoService.criar(c));
    }
    */
}
