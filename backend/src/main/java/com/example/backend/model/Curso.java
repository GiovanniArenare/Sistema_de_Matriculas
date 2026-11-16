package com.example.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Curso {

    private String id;
    private String nome;
    private int creditos;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Curso() {
    }

    public Curso(String id, String nome, int creditos) {
        this.id = id;
        this.nome = nome;
        this.creditos = creditos;
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void removerDisciplina(Disciplina disciplina) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }
}