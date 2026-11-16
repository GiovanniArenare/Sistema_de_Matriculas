package com.example.backend.model;

public class Aluno extends Usuario {

    private String matricula;

    public Aluno() {
        super();
    }

    public Aluno(String id, String nome, String email,
                 String senhaHash, String matricula) {
        super(id, nome, email, senhaHash);
        this.matricula = matricula;
    }

    public void consultarDisciplinas() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void realizarMatricula() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void cancelarMatricula() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}