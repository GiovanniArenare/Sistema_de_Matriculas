
package com.example.backend.controller;

import com.example.backend.model.Curriculo;
import com.example.backend.service.CurriculoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretaria/curriculos")
public class CurriculoController {

    private final CurriculoService curriculoService;

    public CurriculoController(CurriculoService curriculoService) {
        this.curriculoService = curriculoService;
    }

    // Adicionar uma disciplina existente ao currículo
    @PostMapping("/{curriculoId}/disciplinas/{disciplinaId}")
    public ResponseEntity<?> adicionarDisciplina(
            @PathVariable String curriculoId,
            @PathVariable String disciplinaId) {

        try {
            Curriculo curriculo = curriculoService.adicionarDisciplina(
                    curriculoId,
                    disciplinaId);

            return ResponseEntity.ok(curriculo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Listar todos os currículos
    @GetMapping
    public ResponseEntity<List<Curriculo>> listarTodos() {
        return ResponseEntity.ok(curriculoService.listarTodos());
    }

    // Buscar currículo pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(curriculoService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Buscar currículos de um curso
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Curriculo>> buscarPorCurso(
            @PathVariable String cursoId) {

        return ResponseEntity.ok(
                curriculoService.buscarPorCurso(cursoId));
    }

    // Buscar currículo por curso e semestre
    @GetMapping("/curso/{cursoId}/semestre")
    public ResponseEntity<?> buscarPorCursoESemestre(
            @PathVariable String cursoId,
            @RequestParam String semestre) {

        try {
            return ResponseEntity.ok(
                    curriculoService.buscarPorCursoESemestre(
                            cursoId, semestre));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Cadastrar currículo
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody Map<String, String> dados) {

        try {
            Curriculo curriculo = curriculoService.cadastrar(
                    dados.get("cursoId"),
                    dados.get("semestre"));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(curriculo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Remover uma disciplina do currículo
    @DeleteMapping("/{curriculoId}/disciplinas/{disciplinaId}")
    public ResponseEntity<?> removerDisciplina(
            @PathVariable String curriculoId,
            @PathVariable String disciplinaId) {

        try {
            Curriculo curriculo = curriculoService.removerDisciplina(
                    curriculoId,
                    disciplinaId);

            return ResponseEntity.ok(curriculo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

}
