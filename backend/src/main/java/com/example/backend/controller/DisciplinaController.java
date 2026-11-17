
package com.example.backend.controller;

import com.example.backend.model.Disciplina;
import com.example.backend.service.DisciplinaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretaria/disciplinas")
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    public DisciplinaController(DisciplinaService disciplinaService) {
        this.disciplinaService = disciplinaService;
    }

    // Listar todas as disciplinas
    @GetMapping
    public ResponseEntity<List<Disciplina>> listarTodas() {
        return ResponseEntity.ok(disciplinaService.listarTodas());
    }

    // Buscar disciplina pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(disciplinaService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Pesquisar disciplinas pelo nome
    @GetMapping("/buscar")
    public ResponseEntity<List<Disciplina>> buscarPorNome(
            @RequestParam String nome) {

        return ResponseEntity.ok(
                disciplinaService.buscarPorNome(nome)
        );
    }

    // Filtrar disciplinas por créditos
    @GetMapping("/creditos/{creditos}")
    public ResponseEntity<List<Disciplina>> buscarPorCreditos(
            @PathVariable int creditos) {

        return ResponseEntity.ok(
                disciplinaService.buscarPorCreditos(creditos)
        );
    }

    // Cadastrar disciplina
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Disciplina disciplina) {
        try {
            Disciplina salva = disciplinaService.cadastrar(disciplina);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(salva);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Atualizar disciplina
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable String id,
            @RequestBody Disciplina disciplina) {

        try {
            return ResponseEntity.ok(
                    disciplinaService.atualizar(id, disciplina)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Excluir disciplina
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable String id) {
        try {
            disciplinaService.excluir(id);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
