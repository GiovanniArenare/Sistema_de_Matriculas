
package com.example.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.backend.model.Matricula;
import com.example.backend.model.Usuario;
import com.example.backend.model.enums.TipoMatricula;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.service.MatriculaService;

@RestController
@RequestMapping("/api/alunos/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final UsuarioRepository usuarioRepository;

    public MatriculaController(
            MatriculaService matriculaService,
            UsuarioRepository usuarioRepository) {

        this.matriculaService = matriculaService;
        this.usuarioRepository = usuarioRepository;
    }

    // Identificar o aluno autenticado
    private String obterAlunoId(Authentication authentication) {

        if (authentication == null) {
            throw new IllegalStateException(
                    "Usuário não autenticado.");
        }

        String email = authentication.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuário autenticado não encontrado."));

        return usuario.getId();
    }

    // Solicitar matrícula
    @PostMapping
    public ResponseEntity<Matricula> solicitarMatricula(
            @RequestBody SolicitacaoMatriculaRequest request,
            Authentication authentication) {

        String alunoId = obterAlunoId(authentication);

        Matricula matricula = matriculaService.solicitarMatricula(
                alunoId,
                request.ofertaId(),
                request.tipo());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matricula);
    }

    // Consultar matrículas do aluno autenticado
    @GetMapping
    public ResponseEntity<List<Matricula>> listarMinhasMatriculas(
            Authentication authentication) {

        String alunoId = obterAlunoId(authentication);

        List<Matricula> matriculas = matriculaService.listarPorAluno(alunoId);

        return ResponseEntity.ok(matriculas);
    }

    // Dados necessários para solicitar matrícula
    public record SolicitacaoMatriculaRequest(
            String ofertaId,
            TipoMatricula tipo) {
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Matricula> cancelarMatricula(
            @PathVariable String id,
            Authentication authentication) {

        String alunoId = obterAlunoId(authentication);

        Matricula matricula = matriculaService.cancelarMatricula(
                alunoId,
                id);

        return ResponseEntity.ok(matricula);
    }

}
