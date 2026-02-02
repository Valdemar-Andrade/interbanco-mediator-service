package com.kafka.intermediario.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.intermediario.dto.SaldoDTO;
import com.kafka.intermediario.dto.TransacaoDTO;
import com.kafka.intermediario.entities.Banco;
import com.kafka.intermediario.entities.Transacao;
import com.kafka.intermediario.services.implementacao.BancoServiceImpl;
import com.kafka.intermediario.services.implementacao.CodigoEncriptacaoServiceImpl;
import com.kafka.intermediario.services.implementacao.TransacaoServiceServiceImpl;
import com.kafka.intermediario.utils.ArrayTransacaoManipulator;
import com.kafka.intermediario.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class KafkaConsumidor {

    private final ObjectMapper objectMapper;

    @Autowired
    BancoServiceImpl service;

    @Autowired
    TransacaoServiceServiceImpl serviceTransacao;

    @Autowired
    private CodigoEncriptacaoServiceImpl codigoEncriptacaoService;

    @Autowired
    public KafkaConsumidor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private boolean encontrou = false;

    @KafkaListener(topics="interban", groupId = "interban.group.intermediario")
    public void consumidor(String mensagemJson) {

        String chaveString = codigoEncriptacaoService.getChaveFromDB().getCodigo();
        SecretKey secretKey = EncryptionUtil.stringToKey(chaveString);

        TransacaoDTO transacaoDTO;
        SaldoDTO saldoDTO;

        try{
            //Descriptografar a transacao recebida e deserializar para TransacaoDTO
            transacaoDTO = objectMapper.readValue(desencriptarTransacao(mensagemJson, secretKey), TransacaoDTO.class);

            //Descriptografar a transacaoSaldo recebida e deserializar para SaldoDTO
            saldoDTO = objectMapper.readValue(desencriptarTransacao(mensagemJson, secretKey), SaldoDTO.class);

            //--------------------------------------------------------------------------------
            //A transacao partiu do banco BAI - 0040
            if (transacaoDTO.getEstado().equalsIgnoreCase("0040_intermediario")){

                //Validar se o banco destino existe CAF
                for (Banco banco: this.service.findAll()){

                    if (banco.getCaf().equalsIgnoreCase(transacaoDTO.getCaf_destino())){
                        try {
                            enviarParaBancoDestino(transacaoDTO, banco);
                        } catch (JsonProcessingException e) {
                            System.out.println("Erro ao enviar a transacao para o destino");
                        }
                    }
                }

                if (!encontrou){
                    //Destino nao Existe
                    System.out.println("BAI: Destino nao Existe");

                    //Enviar a mensagem para o caf destino BAI - do intermediario para o bai
                    transacaoDTO.setEstado("intermediario_0040_erro");

                    String transacaoJson = null;
                    try {

                        transacaoJson = objectMapper.writeValueAsString(transacaoDTO);

                        String transacaoEncriptada = EncryptionUtil.encrypt(transacaoJson, secretKey);

                        kafkaTemplate.send("interban", transacaoEncriptada);

                    } catch (JsonProcessingException e) {
                        System.out.println("Erro ao enviar a transacao para o destino (externo bai)");
                    }
                    kafkaTemplate.send("interban", transacaoJson);
                }
            }

            //A transacao partiu do banco BFA - 0050
            if (transacaoDTO.getEstado().equalsIgnoreCase("0050_intermediario")){

                //Validar se o banco destino existe CAF
                for (Banco banco: this.service.findAll()){

                    if (banco.getCaf().equalsIgnoreCase(transacaoDTO.getCaf_destino())){
                        try {
                            enviarParaBancoDestino(transacaoDTO, banco);
                            //System.out.println("Estado: " + transacaoDTO.getEstado());
                        } catch (JsonProcessingException e) {
                            System.out.println("Erro ao enviar a transacao para o destino");
                        }
                    }
                }

                if (!encontrou){
                    //Destino nao Existe
                    System.out.println("BFA: Destino nao Existe");

                    //Enviar a mensagem para o caf destino BFA - do intermediario para o bfa
                    transacaoDTO.setEstado("intermediario_0050_erro");

                    String transacaoJson;
                    try {

                        transacaoJson = objectMapper.writeValueAsString(transacaoDTO);

                        String transacaoEncriptada = EncryptionUtil.encrypt(transacaoJson, secretKey);

                        kafkaTemplate.send("interban", transacaoEncriptada);

                    } catch (JsonProcessingException e) {
                        System.out.println("Erro ao enviar a transacao para o destino (externo bfa)");
                    }
                }
            }

            //Manipulacao das requisicoes para consulta de Saldo
            //Recebeu o resultado da consulta do banco BAI
            if (saldoDTO != null && saldoDTO.getEstado().equalsIgnoreCase("0040_intermediario_consulta")){

                //Imprimir o resultado da consulta
                System.out.println("\n\nO resultado da consulta - BAI:\n" + saldoDTO.toString());
q
            }

            //Recebeu o resultado da consulta do banco BFA
            if (saldoDTO != null && saldoDTO.getEstado().equalsIgnoreCase("0050_intermediario_consulta")){

                //Imprimir o resultado da consulta
                System.out.println("\n\nO resultado da consulta - BFA:\n" + saldoDTO.toString());

            }

        }catch (IllegalArgumentException e){}
        catch (JsonMappingException e) {}
        catch (JsonProcessingException e) {}
        catch (Exception e) {}

    }

    private void enviarParaBancoDestino(TransacaoDTO transacaoDTO, Banco banco) throws JsonProcessingException {

        System.out.println("Enviou para " + banco.getCaf());

        encontrou = true;

        //Enviar a mensagem para o caf destino
        transacaoDTO.setEstado("intermediario_" + banco.getCaf());

        ArrayTransacaoManipulator.arrayTransacoes.add(transacaoDTO);

        //Salvar a operacao
        Transacao transacaoSalvar = new Transacao();

        transacaoSalvar.setValor(transacaoDTO.getValor());
        transacaoSalvar.setIbanOrigem(transacaoDTO.getIbanOrigem());
        transacaoSalvar.setIbanDestino(transacaoDTO.getIbanDestino());
        transacaoSalvar.setEstado(transacaoDTO.getEstado());

        serviceTransacao.criar(transacaoSalvar);

        String transacaoJson = objectMapper.writeValueAsString(transacaoDTO);

        //encriptar a mensagem antes de ser enviada
        String chaveString = codigoEncriptacaoService.getChaveFromDB().getCodigo();
        SecretKey secretKey = EncryptionUtil.stringToKey(chaveString);

        String transacaoEncriptada;

        try {

            transacaoEncriptada = EncryptionUtil.encrypt(transacaoJson, secretKey);
            kafkaTemplate.send("interban", transacaoEncriptada);

        } catch (Exception e) {

        }

    }

    private String desencriptarTransacao(String mensagemJson, SecretKey secretKey){

        byte[] mensagemCriptografadaBytes = Base64.getDecoder().decode(mensagemJson);
        String transacaoDesencriptada = null;

        try {
            transacaoDesencriptada = EncryptionUtil.decrypt2(mensagemCriptografadaBytes, secretKey);
        } catch (Exception e) {

        }

        return transacaoDesencriptada;
    }
}
