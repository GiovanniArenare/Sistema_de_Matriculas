package com.example.backend.model;

import java.time.LocalDateTime;

public class NotificacaoCobranca {

    private String id;
    private String alunoId;
    private String semestre;
    private LocalDateTime dataEnvio;
    private String status;

    public NotificacaoCobranca() {
    }

    public NotificacaoCobranca(String id, String alunoId, String semestre) {
        this.id = id;
        this.alunoId = alunoId;
        this.semestre = semestre;
        this.status = "PENDENTE";
    }

    public void enviar() {
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

    public String getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(String alunoId) {
        this.alunoId = alunoId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}