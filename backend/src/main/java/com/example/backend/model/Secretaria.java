package com.example.backend.model;

public class Secretaria extends Usuario {

    private String registroFuncional;

    public Secretaria() {
        super();
    }

    public Secretaria(String id, String nome, String email,
                      String senhaHash, String registroFuncional) {
        super(id, nome, email, senhaHash);
        this.registroFuncional = registroFuncional;
    }

    public void organizarCurriculo() {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public void encerrarPeriodo(String periodoId) {
        throw new UnsupportedOperationException("Método ainda não implementado");
    }

    public String getRegistroFuncional() {
        return registroFuncional;
    }

    public void setRegistroFuncional(String registroFuncional) {
        this.registroFuncional = registroFuncional;
    }
}