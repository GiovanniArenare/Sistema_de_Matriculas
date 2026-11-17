package com.example.backend.model;

import java.util.List;
import com.example.backend.model.enums.TipoMatricula;

public class Aluno extends Usuario {

    private String numeroMatricula;

    public Aluno() {
        super();
    }

    public Aluno(String id, String nome, String email,
                 String senhaHash, String numeroMatricula) {
        super(id, nome, email, senhaHash);
        this.numeroMatricula = numeroMatricula;
    }

    public List<OfertaDisciplina> consultarOfertas() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public Matricula solicitarMatricula(String ofertaId, TipoMatricula tipo) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void cancelarMatricula(String matriculaId) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }
}