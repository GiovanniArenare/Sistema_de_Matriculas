package com.example.backend.model;

import java.time.LocalDateTime;

public class PeriodoMatricula {

    private String id;
    private String semestre;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private boolean encerrado;

    public PeriodoMatricula() {
    }

    public PeriodoMatricula(String id, String semestre,
                            LocalDateTime inicio, LocalDateTime fim) {
        this.id = id;
        this.semestre = semestre;
        this.inicio = inicio;
        this.fim = fim;
        this.encerrado = false;
    }

    public boolean estaAberto(LocalDateTime agora) {
        throw new UnsupportedOperationException(
            "Método ainda não implementado"
        );
    }

    public void encerrar() {
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

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public boolean isEncerrado() {
        return encerrado;
    }

    public void setEncerrado(boolean encerrado) {
        this.encerrado = encerrado;
    }
}