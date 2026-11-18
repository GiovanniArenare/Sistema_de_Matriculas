
package com.example.backend.exception;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornar409QuandoOcorrerConflitoConcorrencia() {

        CannotAcquireLockException excecao = new CannotAcquireLockException(
                "Conflito de escrita no MongoDB");

        ResponseEntity<Map<String, Object>> resposta = handler.tratarConflitoConcorrencia(excecao);

        assertEquals(
                HttpStatus.CONFLICT,
                resposta.getStatusCode());

        assertNotNull(resposta.getBody());

        Map<String, Object> corpo = resposta.getBody();

        assertEquals(409, corpo.get("status"));

        assertEquals(
                "Outra solicitação está sendo processada. "
                        + "Aguarde um instante e tente novamente.",
                corpo.get("mensagem"));

        assertNotNull(corpo.get("timestamp"));
    }

    @Test
    void deveRetornar400QuandoArgumentoForInvalido() {

        IllegalArgumentException excecao = new IllegalArgumentException(
                "Aluno não encontrado.");

        ResponseEntity<Map<String, Object>> resposta = handler.tratarArgumentoInvalido(excecao);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                resposta.getStatusCode());

        assertNotNull(resposta.getBody());

        assertEquals(400, resposta.getBody().get("status"));

        assertEquals(
                "Aluno não encontrado.",
                resposta.getBody().get("mensagem"));
    }

    @Test
    void deveRetornar409QuandoRegraDeNegocioForViolada() {

        IllegalStateException excecao = new IllegalStateException(
                "O aluno já possui matrícula nesta oferta.");

        ResponseEntity<Map<String, Object>> resposta = handler.tratarRegraDeNegocio(excecao);

        assertEquals(
                HttpStatus.CONFLICT,
                resposta.getStatusCode());

        assertNotNull(resposta.getBody());

        assertEquals(409, resposta.getBody().get("status"));

        assertEquals(
                "O aluno já possui matrícula nesta oferta.",
                resposta.getBody().get("mensagem"));
    }

}
