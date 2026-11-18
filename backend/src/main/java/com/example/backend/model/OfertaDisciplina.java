
package com.example.backend.model;

import com.example.backend.model.enums.StatusOferta;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ofertas_disciplinas")
public class OfertaDisciplina {

    @Id
    private String id;

    private String disciplinaId;
    private String professorId;
    private String semestre;

    private int capacidadeMaxima = 60;
    private int quantidadeMatriculados = 0;

    private StatusOferta status;

    public OfertaDisciplina() {
    }

    public OfertaDisciplina(
            String disciplinaId,
            String professorId,
            String semestre) {

        this.disciplinaId = disciplinaId;
        this.professorId = professorId;
        this.semestre = semestre;
        this.status = StatusOferta.ABERTA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(String disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
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

    // Verificar se a oferta possui vagas
    public boolean possuiVagas() {
        return status == StatusOferta.ABERTA
                && quantidadeMatriculados < capacidadeMaxima;
    }

    // Registrar uma nova matrícula
    public void registrarMatricula() {

        if (!possuiVagas()) {
            throw new IllegalStateException(
                    "A oferta não está aberta ou não possui vagas.");
        }

        quantidadeMatriculados++;

        if (quantidadeMatriculados >= capacidadeMaxima) {
            status = StatusOferta.LOTADA;
        }
    }

    // Cancelar uma matrícula existente
    public void cancelarMatricula() {

        if (status != StatusOferta.ABERTA
                && status != StatusOferta.LOTADA) {
            throw new IllegalStateException(
                    "Não é possível cancelar matrículas nesta oferta.");
        }

        if (quantidadeMatriculados <= 0) {
            throw new IllegalStateException(
                    "A oferta não possui matrículas para cancelar.");
        }

        quantidadeMatriculados--;

        if (status == StatusOferta.LOTADA) {
            status = StatusOferta.ABERTA;
        }
    }

    // Finalizar o período de matrículas
    public void finalizarMatriculas() {

        if (status != StatusOferta.ABERTA
                && status != StatusOferta.LOTADA) {
            throw new IllegalStateException(
                    "Esta oferta não pode ser finalizada.");
        }

        if (quantidadeMatriculados < 3) {
            status = StatusOferta.CANCELADA;
        } else {
            status = StatusOferta.ATIVA;
        }
    }

}
