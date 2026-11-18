
package com.example.backend.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.enums.StatusOferta;
import com.mongodb.client.result.UpdateResult;

@Repository
public class OfertaVagaRepository {

    private final MongoTemplate mongoTemplate;

    public OfertaVagaRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // Reservar uma vaga atomicamente

    public boolean reservarVaga(String ofertaId) {

        // Buscar a oferta para obter sua capacidade
        OfertaDisciplina oferta = mongoTemplate.findById(
                ofertaId,
                OfertaDisciplina.class);

        if (oferta == null) {
            return false;
        }

        int capacidade = oferta.getCapacidadeMaxima();

        // Atualizar somente se a capacidade ainda for a mesma
        // e existir uma vaga disponível.
        Query query = new Query(
                Criteria.where("_id").is(ofertaId)
                        .and("status").is(StatusOferta.ABERTA)
                        .and("capacidadeMaxima").is(capacidade)
                        .and("quantidadeMatriculados").lt(capacidade));

        var novaQuantidade = ArithmeticOperators.Add
                .valueOf("quantidadeMatriculados")
                .add(1);

        var novoStatus = ConditionalOperators.when(
                Criteria.where("quantidadeMatriculados")
                        .gte(capacidade - 1))
                .then(StatusOferta.LOTADA.name())
                .otherwise(StatusOferta.ABERTA.name());

        AggregationUpdate update = AggregationUpdate.update()
                .set("quantidadeMatriculados")
                .toValue(novaQuantidade)
                .set("status")
                .toValue(novoStatus);

        UpdateResult resultado = mongoTemplate.updateFirst(
                query,
                update,
                OfertaDisciplina.class);

        return resultado.getModifiedCount() == 1;
    }

    // Devolver uma vaga atomicamente
    public boolean devolverVaga(String ofertaId) {

        Query query = new Query(
                Criteria.where("_id").is(ofertaId)
                        .and("quantidadeMatriculados").gt(0)
                        .and("status").in(
                                StatusOferta.ABERTA,
                                StatusOferta.LOTADA));

        // Diminuir a quantidade de matriculados
        Update update = new Update()
                .inc("quantidadeMatriculados", -1)
                .set("status", StatusOferta.ABERTA);

        UpdateResult resultado = mongoTemplate.updateFirst(
                query,
                update,
                OfertaDisciplina.class);

        return resultado.getModifiedCount() == 1;
    }
}
