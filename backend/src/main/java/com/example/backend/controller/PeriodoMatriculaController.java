
package com.example.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.model.PeriodoMatricula;
import com.example.backend.service.PeriodoMatriculaService;

@RestController
@RequestMapping("/api/secretaria/periodos")
public class PeriodoMatriculaController {

    private final PeriodoMatriculaService service;

    public PeriodoMatriculaController(
            PeriodoMatriculaService service) {
        this.service = service;
    }

    // Listar todos os períodos
    @GetMapping
    public List<PeriodoMatricula> listarTodos() {
        return service.listarTodos();
    }

    // Buscar período pelo semestre
    @GetMapping("/semestre")
    public PeriodoMatricula buscarPorSemestre(
            @RequestParam String semestre) {
        return service.buscarPorSemestre(semestre);
    }

    // Verificar se o período está aberto
    @GetMapping("/aberto")
    public Map<String, Object> verificarAberto(
            @RequestParam String semestre) {

        return Map.of(
                "semestre", semestre,
                "aberto", service.estaAberto(semestre)
        );
    }

    // Buscar período pelo ID
    @GetMapping("/{id}")
    public PeriodoMatricula buscarPorId(
            @PathVariable String id) {
        return service.buscarPorId(id);
    }

    // Cadastrar período
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody CadastroPeriodoDTO dados) {

        try {
            PeriodoMatricula periodo = service.cadastrar(
                    dados.semestre(),
                    dados.inicio(),
                    dados.fim()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(periodo);

        } catch (IllegalArgumentException erro) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", erro.getMessage()));
        }
    }

    // Encerrar período antecipadamente
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<?> encerrar(
            @PathVariable String id) {

        try {
            PeriodoMatricula periodo = service.encerrar(id);

            return ResponseEntity.ok(periodo);

        } catch (IllegalArgumentException erro) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", erro.getMessage()));

        } catch (IllegalStateException erro) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", erro.getMessage()));
        }
    }

    public record CadastroPeriodoDTO(
            String semestre,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
    }
}
