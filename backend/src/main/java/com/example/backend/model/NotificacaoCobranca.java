package com.example.backend.model;

import java.time.LocalDateTime;

public class NotificacaoCobranca {

    private String id;
    private Aluno aluno;
    private String semestre;
    private LocalDateTime dataEnvio;
    private boolean enviada;

    public NotificacaoCobranca() {
    }

    public NotificacaoCobranca(String id, Aluno aluno, String semestre) {
        this.id = id;
        this.aluno = aluno;
        this.semestre = semestre;
        this.enviada = false;
    }

    public void enviarNotificacao() {
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

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public boolean isEnviada() {
        return enviada;
    }

    public void setEnviada(boolean enviada) {
        this.enviada = enviada;
    }
}