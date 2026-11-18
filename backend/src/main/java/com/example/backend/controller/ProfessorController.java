
package com.example.backend.controller;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.service.ProfessorConsultaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.backend.dto.AlunoMatriculadoDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorConsultaService professorConsultaService;

    public ProfessorController(
            ProfessorConsultaService professorConsultaService) {

        this.professorConsultaService = professorConsultaService;
    }

    // Listar as ofertas do professor autenticado
    @GetMapping("/minhas-ofertas")
    public ResponseEntity<List<OfertaDisciplina>> listarMinhasOfertas(
            Authentication authentication) {

        String email = authentication.getName();

        List<OfertaDisciplina> ofertas = professorConsultaService.listarMinhasOfertas(email);

        return ResponseEntity.ok(ofertas);
    }

    @GetMapping("/minhas-ofertas/{ofertaId}/alunos")
    public ResponseEntity<List<AlunoMatriculadoDTO>> listarAlunosMatriculados(
            @PathVariable String ofertaId,
            Authentication authentication) {

        String emailProfessor = authentication.getName();

        List<AlunoMatriculadoDTO> alunos = professorConsultaService.listarAlunosMatriculados(
                emailProfessor,
                ofertaId);

        return ResponseEntity.ok(alunos);
    }

}
