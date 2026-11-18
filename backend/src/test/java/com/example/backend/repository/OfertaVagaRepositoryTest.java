
package com.example.backend.repository;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.enums.StatusOferta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OfertaVagaRepositoryTest {

    @Autowired
    private OfertaVagaRepository vagaRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaRepository;

    @Test
    void deveReservarUltimaVagaEBloquearExcesso() {

        OfertaDisciplina oferta = new OfertaDisciplina(
                "disciplina-teste",
                "professor-teste",
                "TESTE");

        oferta.setQuantidadeMatriculados(59);
        oferta.setStatus(StatusOferta.ABERTA);

        oferta = ofertaRepository.save(oferta);

        String ofertaId = oferta.getId();

        try {
            // Reservar a última vaga
            boolean reservada = vagaRepository.reservarVaga(ofertaId);

            assertTrue(reservada);

            // Consultar a oferta atualizada
            Optional<OfertaDisciplina> resultado = ofertaRepository.findById(ofertaId);

            assertTrue(resultado.isPresent());

            OfertaDisciplina atualizada = resultado.get();

            assertEquals(60, atualizada.getQuantidadeMatriculados());
            assertEquals(StatusOferta.LOTADA, atualizada.getStatus());

            // Tentar reservar uma vaga adicional
            boolean segundaReserva = vagaRepository.reservarVaga(ofertaId);

            assertFalse(segundaReserva);

        } finally {
            // Remover a oferta temporária mesmo se o teste falhar
            ofertaRepository.deleteById(ofertaId);
        }
    }

    @Test
    void deveRespeitarCapacidadeMaximaPersonalizada() {

        OfertaDisciplina oferta = new OfertaDisciplina(
                "disciplina-teste",
                "professor-teste",
                "TESTE-CAPACIDADE");

        oferta.setCapacidadeMaxima(10);
        oferta.setQuantidadeMatriculados(9);
        oferta.setStatus(StatusOferta.ABERTA);

        oferta = ofertaRepository.save(oferta);

        String ofertaId = oferta.getId();

        try {
            // Reservar a décima vaga
            boolean reservada = vagaRepository.reservarVaga(ofertaId);

            assertTrue(reservada);

            OfertaDisciplina atualizada = ofertaRepository.findById(ofertaId).orElseThrow();

            assertEquals(10, atualizada.getQuantidadeMatriculados());
            assertEquals(StatusOferta.LOTADA, atualizada.getStatus());

            // Impedir a décima primeira reserva
            boolean segundaReserva = vagaRepository.reservarVaga(ofertaId);

            assertFalse(segundaReserva);

        } finally {
            ofertaRepository.deleteById(ofertaId);
        }
    }

}
