package com.example.backend.repository;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.enums.StatusOferta;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OfertaDisciplinaRepository
        extends MongoRepository<OfertaDisciplina, String> {

    // Buscar ofertas de uma disciplina
    List<OfertaDisciplina> findByDisciplinaId(String disciplinaId);

    // Buscar ofertas de um professor
    List<OfertaDisciplina> findByProfessorId(String professorId);

    // Buscar ofertas de determinado semestre
    List<OfertaDisciplina> findBySemestre(String semestre);

    // Buscar ofertas pelo status
    List<OfertaDisciplina> findByStatus(StatusOferta status);

    // Buscar ofertas de uma disciplina em determinado semestre
    List<OfertaDisciplina> findByDisciplinaIdAndSemestre(
            String disciplinaId,
            String semestre
    );
}
