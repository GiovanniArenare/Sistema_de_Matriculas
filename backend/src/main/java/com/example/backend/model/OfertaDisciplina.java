package com.example.backend.model;

import com.example.backend.model.enums.StatusOferta;

public class OfertaDisciplina {

    private String id;
    private Disciplina disciplina;
    private Professor professor;
    private String semestre;
    private int capacidadeMaxima = 60;
    private int quantidadeMatriculados;
    private StatusOferta status = StatusOferta.PLANEJADA;

    public OfertaDisciplina() {
    }

    public OfertaDisciplina(String id, Disciplina disciplina,
                           Professor professor, String semestre) {
        this.id = id;
        this.disciplina = disciplina;
        this.professor = professor;
        this.semestre = semestre;
        this.capacidadeMaxima = 60;
        this.quantidadeMatriculados = 0;
        this.status = StatusOferta.PLANEJADA;
    }

    public int contarMatriculados() {
        throw new UnsupportedOperationException(
            "Método ainda não implementado"
        );
    }

    public boolean possuiVagas() {
        throw new UnsupportedOperationException(
            "Método ainda não implementado"
        );
    }

    public void definirStatusFinal() {
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

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public int getQuantidadeMatriculados() {
        return quantidadeMatriculados;
    }

    public void setQuantidadeMatriculados(int quantidadeMatriculados) {
        this.quantidadeMatriculados = quantidadeMatriculados;
    }

    public StatusOferta getStatus() {
        return status;
    }

    public void setStatus(StatusOferta status) {
        this.status = status;
    }
}