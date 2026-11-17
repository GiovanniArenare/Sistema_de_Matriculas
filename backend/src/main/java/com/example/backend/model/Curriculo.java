
package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "curriculos")
public class Curriculo {

    @Id
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

    public void adicionarDisciplina(String disciplinaId) {
        throw new UnsupportedOperationException(
                "Método ainda não implementado"
        );
    }

    public void removerDisciplina(String disciplinaId) {
        throw new UnsupportedOperationException(
                "Método ainda não implementado"
        );
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
