
package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "curriculos")
public class Curriculo {

    @Id
    private String id;

    private String cursoId;
    private String semestre;
    private List<String> disciplinaIds = new ArrayList<>();

    public Curriculo() {
    }

    public Curriculo(String id, String cursoId, String semestre) {
        this.id = id;
        this.cursoId = cursoId;
        this.semestre = semestre;
    }

    public void adicionarDisciplina(String disciplinaId) {

        if (disciplinaId == null || disciplinaId.isBlank()) {
            throw new IllegalArgumentException(
                    "O ID da disciplina é obrigatório."
            );
        }

        if (disciplinaIds.contains(disciplinaId)) {
            throw new IllegalArgumentException(
                    "Esta disciplina já pertence ao currículo."
            );
        }

        disciplinaIds.add(disciplinaId);
    }

    public void removerDisciplina(String disciplinaId) {

        if (!disciplinaIds.remove(disciplinaId)) {
            throw new IllegalArgumentException(
                    "Esta disciplina não pertence ao currículo."
            );
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCursoId() {
        return cursoId;
    }

    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public List<String> getDisciplinaIds() {
        return disciplinaIds;
    }

    public void setDisciplinaIds(List<String> disciplinaIds) {
        this.disciplinaIds = disciplinaIds == null
                ? new ArrayList<>()
                : new ArrayList<>(disciplinaIds);
    }
}
