package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PermissaoTesteController {

    @GetMapping("/alunos/teste")
    public ResponseEntity<?> testarAluno(
            Authentication authentication) {

        return ResponseEntity.ok(Map.of(
                "mensagem", "Acesso autorizado para aluno!",
                "usuario", authentication.getName()
        ));
    }

    @GetMapping("/professores/teste")
    public ResponseEntity<?> testarProfessor(
            Authentication authentication) {

        return ResponseEntity.ok(Map.of(
                "mensagem", "Acesso autorizado para professor!",
                "usuario", authentication.getName()
        ));
    }

    @GetMapping("/secretaria/teste")
    public ResponseEntity<?> testarSecretaria(
            Authentication authentication) {

        return ResponseEntity.ok(Map.of(
                "mensagem", "Acesso autorizado para secretaria!",
                "usuario", authentication.getName()
        ));
    }
}