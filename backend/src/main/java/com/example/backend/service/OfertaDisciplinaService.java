
package com.example.backend.service;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.Professor;
import com.example.backend.repository.DisciplinaRepository;
import com.example.backend.repository.OfertaDisciplinaRepository;
import com.example.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfertaDisciplinaService {

    private final OfertaDisciplinaRepository ofertaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final UsuarioRepository usuarioRepository;

    public OfertaDisciplinaService(
            OfertaDisciplinaRepository ofertaRepository,
            DisciplinaRepository disciplinaRepository,
            UsuarioRepository usuarioRepository) {

        this.ofertaRepository = ofertaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Listar todas as ofertas
    public List<OfertaDisciplina> listarTodas() {
        return ofertaRepository.findAll();
    }

    // Buscar oferta pelo ID
    public OfertaDisciplina buscarPorId(String id) {
        return ofertaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Oferta de disciplina não encontrada."));
    }

    // Buscar ofertas por semestre
    public List<OfertaDisciplina> buscarPorSemestre(String semestre) {
        return ofertaRepository.findBySemestre(semestre);
    }

    // Buscar ofertas de uma disciplina
    public List<OfertaDisciplina> buscarPorDisciplina(String disciplinaId) {
        return ofertaRepository.findByDisciplinaId(disciplinaId);
    }

    // Buscar ofertas atribuídas a um professor
    public List<OfertaDisciplina> buscarPorProfessor(String professorId) {

        if (professorId == null || professorId.isBlank()) {
            throw new IllegalArgumentException(
                    "O professor é obrigatório.");
        }

        return ofertaRepository.findByProfessorId(professorId.trim());
    }

    // Cadastrar nova oferta
    public OfertaDisciplina cadastrar(
            String disciplinaId,
            String professorId,
            String semestre) {

        if (disciplinaId == null || disciplinaId.isBlank()) {
            throw new IllegalArgumentException(
                    "A disciplina é obrigatória.");
        }

        if (professorId == null || professorId.isBlank()) {
            throw new IllegalArgumentException(
                    "O professor é obrigatório.");
        }

        if (semestre == null || semestre.isBlank()) {
            throw new IllegalArgumentException(
                    "O semestre é obrigatório.");
        }

        disciplinaId = disciplinaId.trim();
        professorId = professorId.trim();
        semestre = semestre.trim();

        // Verificar se a disciplina existe
        if (!disciplinaRepository.existsById(disciplinaId)) {
            throw new IllegalArgumentException(
                    "Disciplina não encontrada.");
        }

        // Verificar se o usuário é um professor
        var usuario = usuarioRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Professor não encontrado."));

        if (!(usuario instanceof Professor)) {
            throw new IllegalArgumentException(
                    "O usuário informado não é um professor.");
        }

        // Criar oferta com os valores iniciais
        OfertaDisciplina oferta = new OfertaDisciplina(
                disciplinaId,
                professorId,
                semestre);

        oferta.setCapacidadeMaxima(60);
        oferta.setQuantidadeMatriculados(0);

        return ofertaRepository.save(oferta);
    }
}
