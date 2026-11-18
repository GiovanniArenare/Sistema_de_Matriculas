
package com.example.backend.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Usuario;
import com.mongodb.client.result.UpdateResult;

@Repository
public class MatriculaConcorrenciaRepository {

    private final MongoTemplate mongoTemplate;

    public MatriculaConcorrenciaRepository(
            MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Atualizar um documento compartilhado pelo aluno
    public boolean registrarTentativa(String alunoId) {

        Query query = new Query(
                Criteria.where("_id").is(alunoId)
        );

        Update update = new Update()
                .inc("versaoMatriculas", 1);

        UpdateResult resultado = mongoTemplate.updateFirst(
                query,
                update,
                Usuario.class
        );

        return resultado.getModifiedCount() == 1;
    }
}
