
package com.example.backend.controller;

import com.example.backend.dto.CadastroUsuarioDTO;
import com.example.backend.factory.UsuarioFactory;
import com.example.backend.model.Usuario;
import com.example.backend.model.enums.PerfilUsuario;
import com.example.backend.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/secretaria/professores")
public class SecretariaProfessorController {

    private final UsuarioService usuarioService;
    private final UsuarioFactory usuarioFactory;

    public SecretariaProfessorController(
            UsuarioService usuarioService,
            UsuarioFactory usuarioFactory) {

        this.usuarioService = usuarioService;
        this.usuarioFactory = usuarioFactory;
    }

    @PostMapping
    public ResponseEntity<?> cadastrarProfessor(
            @Valid @RequestBody CadastroUsuarioDTO dto) {

        if (dto.perfil() != PerfilUsuario.PROFESSOR) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "erro", "Este endpoint permite apenas professores."
                    ));
        }

        if (dto.registro() == null || dto.registro().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "erro", "O registro do professor é obrigatório."
                    ));
        }

        try {
            Usuario usuario = usuarioFactory.criar(dto);

            Usuario salvo = usuarioService.cadastrar(
                    usuario,
                    dto.senha()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "mensagem", "Professor cadastrado com sucesso!",
                            "id", salvo.getId(),
                            "nome", salvo.getNome(),
                            "email", salvo.getEmail()
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "erro", e.getMessage()
                    ));
        }
    }
}
