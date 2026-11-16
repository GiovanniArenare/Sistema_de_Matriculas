package com.example.backend.model;

public class Disciplina {

    private String id;
    private String nome;
    private String codigo;
    private int creditos;

    public Disciplina() {
    }

    public Disciplina(String id, String nome, String codigo, int creditos) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.creditos = creditos;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }
}