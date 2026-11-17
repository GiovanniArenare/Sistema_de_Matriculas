
package com.example.backend.controller;

import com.example.backend.model.Curso;
import com.example.backend.service.CursoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretaria/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // Listar todos os cursos
    @GetMapping
    public ResponseEntity<List<Curso>> listarTodos() {
        return ResponseEntity.ok(cursoService.listarTodos());
    }

    // Buscar curso pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(cursoService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Pesquisar cursos pelo nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Curso>> buscarPorNome(
            @RequestParam String nome) {

        return ResponseEntity.ok(cursoService.buscarPorNome(nome));
    }

    // Cadastrar curso
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Curso curso) {
        try {
            Curso salvo = cursoService.cadastrar(curso);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(salvo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Atualizar curso
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable String id,
            @RequestBody Curso curso) {

        try {
            return ResponseEntity.ok(cursoService.atualizar(id, curso));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Excluir curso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable String id) {
        try {
            cursoService.excluir(id);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
