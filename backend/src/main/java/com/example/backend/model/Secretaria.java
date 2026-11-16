package com.example.backend.model;

public class Secretaria extends Usuario {

    public Secretaria() {
        super();
    }

    public Secretaria(String id, String nome, String email,
                      String senhaHash) {
        super(id, nome, email, senhaHash);
    }

    public void gerenciarAlunos() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void gerenciarProfessores() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void gerenciarCursos() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void gerenciarDisciplinas() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void gerenciarCurriculo() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void gerenciarPeriodosMatricula() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }
}