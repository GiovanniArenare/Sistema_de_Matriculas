
package com.example.backend.model;

import com.example.backend.model.enums.StatusOferta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfertaDisciplinaTest {

    private OfertaDisciplina criarOferta() {
        return new OfertaDisciplina(
                "disciplina-teste",
                "professor-teste",
                "2027/1");
    }

    @Test
    void deveIniciarComSessentaVagas() {
        OfertaDisciplina oferta = criarOferta();

        assertEquals(60, oferta.getCapacidadeMaxima());
        assertEquals(0, oferta.getQuantidadeMatriculados());
        assertEquals(StatusOferta.ABERTA, oferta.getStatus());
        assertTrue(oferta.possuiVagas());
    }

    @Test
    void deveFicarLotadaAoAtingirSessentaAlunos() {
        OfertaDisciplina oferta = criarOferta();

        for (int i = 0; i < 60; i++) {
            oferta.registrarMatricula();
        }

        assertEquals(60, oferta.getQuantidadeMatriculados());
        assertEquals(StatusOferta.LOTADA, oferta.getStatus());
        assertFalse(oferta.possuiVagas());
    }

    @Test
    void naoDevePermitirMaisDeSessentaAlunos() {
        OfertaDisciplina oferta = criarOferta();

        for (int i = 0; i < 60; i++) {
            oferta.registrarMatricula();
        }

        assertThrows(
                IllegalStateException.class,
                oferta::registrarMatricula);

        assertEquals(60, oferta.getQuantidadeMatriculados());
    }

    @Test
    void deveReabrirVagaAposCancelamento() {
        OfertaDisciplina oferta = criarOferta();

        for (int i = 0; i < 60; i++) {
            oferta.registrarMatricula();
        }

        oferta.cancelarMatricula();

        assertEquals(59, oferta.getQuantidadeMatriculados());
        assertEquals(StatusOferta.ABERTA, oferta.getStatus());
    }

    @Test
    void deveCancelarOfertaComMenosDeTresAlunos() {
        OfertaDisciplina oferta = criarOferta();

        oferta.registrarMatricula();
        oferta.registrarMatricula();

        oferta.finalizarMatriculas();

        assertEquals(StatusOferta.CANCELADA, oferta.getStatus());
    }

    @Test
    void deveAtivarOfertaComTresAlunos() {
        OfertaDisciplina oferta = criarOferta();

        for (int i = 0; i < 3; i++) {
            oferta.registrarMatricula();
        }

        oferta.finalizarMatriculas();

        assertEquals(StatusOferta.ATIVA, oferta.getStatus());
    }

    @Test
    void naoDevePermitirMatriculaEmOfertaCancelada() {
        OfertaDisciplina oferta = criarOferta();

        oferta.finalizarMatriculas();

        assertThrows(
                IllegalStateException.class,
                oferta::registrarMatricula);
    }

    @Test
    void deveRespeitarCapacidadeMaximaPersonalizada() {
        OfertaDisciplina oferta = criarOferta();

        oferta.setCapacidadeMaxima(10);

        for (int i = 0; i < 10; i++) {
            oferta.registrarMatricula();
        }

        assertEquals(10, oferta.getQuantidadeMatriculados());
        assertEquals(StatusOferta.LOTADA, oferta.getStatus());
        assertFalse(oferta.possuiVagas());
    }

    @Test
    void devePermitirNovaMatriculaAposCancelarUmaVaga() {
        OfertaDisciplina oferta = criarOferta();

        oferta.setCapacidadeMaxima(2);

        oferta.registrarMatricula();
        oferta.registrarMatricula();

        oferta.cancelarMatricula();
        oferta.registrarMatricula();

        assertEquals(2, oferta.getQuantidadeMatriculados());
        assertEquals(StatusOferta.LOTADA, oferta.getStatus());
    }
}
