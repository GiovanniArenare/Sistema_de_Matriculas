package com.example.backend.model;

import java.time.LocalDateTime;
import com.example.backend.model.enums.TipoMatricula;
import com.example.backend.model.enums.StatusMatricula;

public class Matricula {

    private String id;
    private Aluno aluno;
    private OfertaDisciplina oferta;
    private TipoMatricula tipo;
    private StatusMatricula status;
    private LocalDateTime dataMatricula;

    public Matricula() {
    }

    public Matricula(String id, Aluno aluno,
                     OfertaDisciplina oferta, TipoMatricula tipo) {
        this.id = id;
        this.aluno = aluno;
        this.oferta = oferta;
        this.tipo = tipo;
        this.status = StatusMatricula.ATIVA;
        this.dataMatricula = LocalDateTime.now();
    }

    public void cancelar() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public OfertaDisciplina getOferta() {
        return oferta;
    }

    public void setOferta(OfertaDisciplina oferta) {
        this.oferta = oferta;
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public void setTipo(TipoMatricula tipo) {
        this.tipo = tipo;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    public LocalDateTime getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDateTime dataMatricula) {
        this.dataMatricula = dataMatricula;
    }
}