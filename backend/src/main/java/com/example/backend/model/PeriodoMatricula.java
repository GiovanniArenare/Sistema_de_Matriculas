package com.example.backend.model;

import java.time.LocalDate;

public class PeriodoMatricula {

    private String id;
    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public PeriodoMatricula() {
    }

    public PeriodoMatricula(String id, String semestre,
                            LocalDate dataInicio, LocalDate dataFim) {
        this.id = id;
        this.semestre = semestre;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public boolean estaAberto() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}