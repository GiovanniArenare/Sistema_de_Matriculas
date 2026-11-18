
package com.example.backend.service;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.enums.StatusOferta;
import com.example.backend.repository.OfertaDisciplinaRepository;
import com.example.backend.repository.OfertaVagaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransacaoMatriculaTest {

    @Autowired
    private OfertaDisciplinaRepository ofertaRepository;

    @Autowired
    private OfertaVagaRepository ofertaVagaRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void deveDesfazerReservaQuandoTransacaoFalhar() {

        OfertaDisciplina oferta = new OfertaDisciplina();

        oferta.setDisciplinaId("disciplina-teste");
        oferta.setProfessorId("professor-teste");
        oferta.setSemestre("TESTE-ROLLBACK");
        oferta.setCapacidadeMaxima(60);
        oferta.setQuantidadeMatriculados(0);
        oferta.setStatus(StatusOferta.ABERTA);

        oferta = ofertaRepository.save(oferta);

        String ofertaId = oferta.getId();

        try {

            assertThrows(
                    IllegalStateException.class,
                    () -> transactionTemplate.execute(status -> {

                        boolean reservada = ofertaVagaRepository.reservarVaga(ofertaId);

                        assertTrue(reservada);

                        // Simular falha após reservar a vaga
                        throw new IllegalStateException(
                                "Falha simulada ao salvar matrícula");
                    }));

            OfertaDisciplina atualizada = ofertaRepository.findById(ofertaId)
                    .orElseThrow();

            // A transação deve ter sido desfeita
            assertEquals(
                    0,
                    atualizada.getQuantidadeMatriculados());

            assertEquals(
                    StatusOferta.ABERTA,
                    atualizada.getStatus());

        } finally {

            // Remover somente a oferta temporária
            ofertaRepository.deleteById(ofertaId);
        }
    }

    @Test
    void deveDesfazerDevolucaoQuandoTransacaoFalhar() {

        OfertaDisciplina oferta = new OfertaDisciplina();

        oferta.setDisciplinaId("disciplina-teste");
        oferta.setProfessorId("professor-teste");
        oferta.setSemestre("TESTE-ROLLBACK-CANCELAMENTO");
        oferta.setCapacidadeMaxima(60);
        oferta.setQuantidadeMatriculados(1);
        oferta.setStatus(StatusOferta.ABERTA);

        oferta = ofertaRepository.save(oferta);

        String ofertaId = oferta.getId();

        try {

            assertThrows(
                    IllegalStateException.class,
                    () -> transactionTemplate.execute(status -> {

                        // Devolver uma vaga
                        boolean devolvida = ofertaVagaRepository.devolverVaga(ofertaId);

                        assertTrue(devolvida);

                        // Simular falha após devolver a vaga
                        throw new IllegalStateException(
                                "Falha simulada ao cancelar matrícula");
                    }));

            // Consultar novamente a oferta no MongoDB
            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            // A devolução deve ter sido desfeita
            assertEquals(
                    1,
                    atualizada.getQuantidadeMatriculados());

            assertEquals(
                    StatusOferta.ABERTA,
                    atualizada.getStatus());

        } finally {

            // Remover somente a oferta temporária
            ofertaRepository.deleteById(ofertaId);
        }
    }

}
