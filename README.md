# interbanco-mediator-service

## Visão Geral
O **interbanco-mediator-service** é o serviço central do sistema **InterBanco**, responsável por **orquestrar as transferências financeiras** entre instituições bancárias participantes.

Ele atua como um **mediador**, coordenando a comunicação entre os serviços bancários por meio de **mensageria com Apache Kafka**, garantindo desacoplamento, escalabilidade e resiliência no processamento das transações.

---

## Papel na Arquitetura
No ecossistema InterBanco, o Mediator Service é responsável por:

- Receber solicitações de transferência via **APIs REST**
- Validar dados e regras iniciais da transação
- Publicar eventos de transferência no Kafka
- Orquestrar o fluxo entre os bancos participantes
- Receber eventos de confirmação e consolidar o processo

Este serviço representa o **ponto de entrada** do sistema distribuído.

---

## Tecnologias Utilizadas
- Java  
- Spring Boot  
- Apache Kafka  
- Maven  

---

## Fluxo de Funcionamento
1. Uma requisição de transferência é enviada ao Mediator via API REST.
2. O Mediator valida os dados iniciais da transação.
3. Um evento de solicitação de transferência é publicado no Kafka.
4. Os serviços bancários consomem o evento e processam a operação.
5. Eventos de confirmação retornam ao Mediator.
6. O fluxo da transferência é finalizado.

---

## Como Executar Localmente

### Pré-requisitos
- Java 17+
- Maven
- Apache Kafka em execução

### Passos
```bash
git clone https://github.com/Valdemar-Andrade/interbanco-mediator-service.git
cd interbanco-mediator-service
mvn clean install
mvn spring-boot:run
```

## Comunicação com Kafka
- Publicação de eventos de solicitação de transferência
- Consumo de eventos de confirmação dos bancos
- Comunicação assíncrona para orquestração de fluxos

## API REST
- O serviço expõe endpoints REST para:
- Iniciação de transferências financeiras
- Consulta do status das operações

## Observações Técnicas
- Arquitetura orientada a eventos para reduzir acoplamento.
- Uso de mensageria para melhorar escalabilidade e tolerância a falhas.
- Separação clara de responsabilidades entre orquestração e processamento.
- Projeto desenvolvido para fins educacionais e demonstração de arquitetura backend.

## Projetos Relacionados
- [interbanco-bank-service-a](https://github.com/Valdemar-Andrade/interbanco-bank-service-a) — Serviço bancário participante do sistema InterBanco.
- [interbanco-bank-service-b](https://github.com/Valdemar-Andrade/interbanco-bank-service-b) — Serviço bancário participante do sistema InterBanco.

- [Meu LinkedIn](https://www.linkedin.com/in/valdemar-andrade-8b0b45189) - LinkedIn







