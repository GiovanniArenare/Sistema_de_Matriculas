package com.example.backend.model;

public class Professor extends Usuario {

    private String registro;

    public Professor() {
        super();
    }

    public Professor(String id, String nome, String email,
                     String senhaHash, String registro) {
        super(id, nome, email, senhaHash);
        this.registro = registro;
    }

    public void consultarDisciplinas() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void consultarAlunos() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }
}