
package com.example.backend.controller;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.service.OfertaDisciplinaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretaria/ofertas")
public class OfertaDisciplinaController {

    private final OfertaDisciplinaService ofertaService;

    public OfertaDisciplinaController(
            OfertaDisciplinaService ofertaService) {

        this.ofertaService = ofertaService;
    }

    // Listar todas as ofertas
    @GetMapping
    public List<OfertaDisciplina> listarTodas() {
        return ofertaService.listarTodas();
    }

    // Buscar oferta pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(ofertaService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Buscar ofertas por semestre
    @GetMapping("/semestre")
    public List<OfertaDisciplina> buscarPorSemestre(
            @RequestParam String semestre) {

        return ofertaService.buscarPorSemestre(semestre);
    }

    // Buscar ofertas de uma disciplina
    @GetMapping("/disciplina/{disciplinaId}")
    public List<OfertaDisciplina> buscarPorDisciplina(
            @PathVariable String disciplinaId) {

        return ofertaService.buscarPorDisciplina(disciplinaId);
    }

    // Cadastrar nova oferta
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody CadastroOfertaDTO dados) {

        try {
            OfertaDisciplina oferta = ofertaService.cadastrar(
                    dados.disciplinaId(),
                    dados.professorId(),
                    dados.semestre()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(oferta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // DTO para receber os dados do cadastro
    public record CadastroOfertaDTO(
            String disciplinaId,
            String professorId,
            String semestre
    ) {
    }

}
