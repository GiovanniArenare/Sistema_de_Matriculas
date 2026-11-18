
package com.example.backend.repository;

import com.example.backend.model.Matricula;
import com.example.backend.model.enums.StatusMatricula;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class MatriculaStatusRepository {

    private final MongoTemplate mongoTemplate;

    public MatriculaStatusRepository(
            MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Cancelar somente se a matrícula ainda estiver inscrita
    // e pertencer ao aluno informado.
    public boolean cancelarSeInscrita(
            String matriculaId,
            String alunoId) {

        Query query = new Query(
                Criteria.where("_id").is(matriculaId)
                        .and("alunoId").is(alunoId)
                        .and("status").is(StatusMatricula.INSCRITA)
        );

        Update update = new Update()
                .set("status", StatusMatricula.CANCELADA);

        UpdateResult resultado = mongoTemplate.updateFirst(
                query,
                update,
                Matricula.class
        );

        return resultado.getModifiedCount() == 1;
    }
}
