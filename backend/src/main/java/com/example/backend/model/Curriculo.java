package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Curriculo {

    private String id;
    private Curso curso;
    private String semestre;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Curriculo() {
    }

    public Curriculo(String id, Curso curso, String semestre) {
        this.id = id;
        this.curso = curso;
        this.semestre = semestre;
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void removerDisciplina(Disciplina disciplina) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
}