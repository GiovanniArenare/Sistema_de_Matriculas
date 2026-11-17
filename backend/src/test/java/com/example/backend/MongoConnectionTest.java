package com.example.backend;

import com.mongodb.client.MongoClient;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MongoConnectionTest {

    @Autowired
    private MongoClient mongoClient;

    @Test
    void testarConexaoMongoDB() {

        Document resultado = mongoClient
                .getDatabase("matriculas")
                .runCommand(new Document("ping", 1));

        Number ok = (Number) resultado.get("ok");

        assertEquals(1, ok.intValue());

        System.out.println("MongoDB Atlas conectado com sucesso!");
    }
}