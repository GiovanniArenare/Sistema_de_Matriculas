
package com.example.backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.CannotAcquireLockException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(
            IllegalArgumentException excecao) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                excecao.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> tratarRegraDeNegocio(
            IllegalStateException excecao) {

        return criarResposta(
                HttpStatus.CONFLICT,
                excecao.getMessage());
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ResponseEntity<Map<String, Object>> tratarConflitoConcorrencia(
            CannotAcquireLockException excecao) {

        return criarResposta(
                HttpStatus.CONFLICT,
                "Outra solicitação está sendo processada. "
                        + "Aguarde um instante e tente novamente.");
    }

    private ResponseEntity<Map<String, Object>> criarResposta(
            HttpStatus status,
            String mensagem) {

        Map<String, Object> resposta = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "erro", status.getReasonPhrase(),
                "mensagem", mensagem);

        return ResponseEntity
                .status(status)
                .body(resposta);
    }
}
