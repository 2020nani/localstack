package com.example.app;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class QueueConsumer {

    private final S3Client s3 = S3Client.builder()
            .endpointOverride(java.net.URI.create("http://localhost:4566"))
            .region(software.amazon.awssdk.regions.Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

    private final String bucket = "send-message-bucket";

    @SqsListener("send-message")
    public void receive(String message){
        System.out.println("Mensagem Recebida");
        try{
            String key = "msg-" + Instant.now().toEpochMilli() + ".json";

            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("application/json")
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromString(message, StandardCharsets.UTF_8));

            String url = "http://localhost:4566/" + bucket + "/" + key;
            System.out.println("JSON salvo no S3: " + url);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
