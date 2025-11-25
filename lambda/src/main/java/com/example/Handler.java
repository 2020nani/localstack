package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.client.MongoCollection;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, String> {

    private final SqsClient sqs = SqsClient.builder()
            .endpointOverride(URI.create("http://localhost:4566")) // chave aqui
            .region(Region.US_EAST_1)
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("test", "test")
                    )
            )
            .build();
    private final RestTemplate rest;

    public Handler() {

        // REST client
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000);
        requestFactory.setReadTimeout(2000);

        this.rest = new RestTemplate(requestFactory);

        // Database
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(System.getenv("DB_URL"));
        ds.setUsername(System.getenv("DB_USER"));
        ds.setPassword(System.getenv("DB_PASS"));
        ds.setMaximumPoolSize(2);

        this.dataSource = ds;
    }



    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        // Extrai o campo "message" do JSON enviado
        String message = (String) input.get("message");
        MongoCollection<MessageDocument> collection =
                MongoConnection.getDb().getCollection("messages", MessageDocument.class);

        MessageDocument doc = new MessageDocument(
                (String) input.get("message"),
                System.currentTimeMillis()
        );

        collection.insertOne(doc);

        return "Inserido com id: " + doc.getId();

        // Nome correto da variável de ambiente
        String queueUrl = System.getenv("QUEUE_URL");

        sqs.sendMessage(
                SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody("Mensagem da Lambda: " + message)
                        .build()
        );

        return "OK";
    }
}
