package com.example.backend.model;

import java.util.List;

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

    public List<OfertaDisciplina> consultarOfertas() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public List<Aluno> consultarMatriculados(String ofertaId) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }
}