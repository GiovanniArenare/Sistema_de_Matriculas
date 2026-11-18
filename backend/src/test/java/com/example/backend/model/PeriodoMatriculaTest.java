
package com.example.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PeriodoMatriculaTest {

    private final LocalDateTime inicio =
            LocalDateTime.of(2027, 2, 1, 8, 0);

    private final LocalDateTime fim =
            LocalDateTime.of(2027, 2, 10, 23, 59);

    private PeriodoMatricula criarPeriodo() {
        return new PeriodoMatricula("2027/1", inicio, fim);
    }

    @Test
    void deveIniciarAbertoParaMatriculas() {
        PeriodoMatricula periodo = criarPeriodo();

        assertFalse(periodo.isEncerrado());
        assertTrue(periodo.estaAberto(
                LocalDateTime.of(2027, 2, 5, 12, 0)
        ));
    }

    @Test
    void naoDevePermitirMatriculaAntesDoInicio() {
        PeriodoMatricula periodo = criarPeriodo();

        assertFalse(periodo.estaAberto(
                LocalDateTime.of(2027, 1, 31, 12, 0)
        ));
    }

    @Test
    void naoDevePermitirMatriculaAposOTermino() {
        PeriodoMatricula periodo = criarPeriodo();

        assertFalse(periodo.estaAberto(
                LocalDateTime.of(2027, 2, 11, 12, 0)
        ));
    }

    @Test
    void devePermitirMatriculaNosLimitesDoPeriodo() {
        PeriodoMatricula periodo = criarPeriodo();

        assertTrue(periodo.estaAberto(inicio));
        assertTrue(periodo.estaAberto(fim));
    }

    @Test
    void deveBloquearMatriculasAposEncerramento() {
        PeriodoMatricula periodo = criarPeriodo();

        periodo.encerrar();

        assertTrue(periodo.isEncerrado());

        assertFalse(periodo.estaAberto(
                LocalDateTime.of(2027, 2, 5, 12, 0)
        ));
    }

    @Test
    void naoDevePermitirEncerrarDuasVezes() {
        PeriodoMatricula periodo = criarPeriodo();

        periodo.encerrar();

        assertThrows(
                IllegalStateException.class,
                periodo::encerrar
        );
    }

    @Test
    void naoDevePermitirDatasInvalidas() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PeriodoMatricula(
                        "2027/1",
                        fim,
                        inicio
                )
        );
    }

    @Test
    void naoDevePermitirDatasNulas() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PeriodoMatricula(
                        "2027/1",
                        null,
                        fim
                )
        );
    }
}
