
package com.example.backend.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "periodos_matricula")
public class PeriodoMatricula {

    @Id
    private String id;

    private String semestre;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private boolean encerrado;

    public PeriodoMatricula() {
    }

    public PeriodoMatricula(
            String semestre,
            LocalDateTime inicio,
            LocalDateTime fim) {

        validarDatas(inicio, fim);

        this.semestre = semestre;
        this.inicio = inicio;
        this.fim = fim;
        this.encerrado = false;
    }

    // Verificar se o período está aberto
    public boolean estaAberto(LocalDateTime agora) {

        if (agora == null || inicio == null || fim == null) {
            return false;
        }

        return !encerrado
                && !agora.isBefore(inicio)
                && !agora.isAfter(fim);
    }

    // Encerrar o período antecipadamente
    public void encerrar() {

        if (encerrado) {
            throw new IllegalStateException(
                    "O período de matrícula já está encerrado."
            );
        }

        encerrado = true;
    }

    // Validar o intervalo de datas
    private void validarDatas(
            LocalDateTime inicio,
            LocalDateTime fim) {

        if (inicio == null || fim == null) {
            throw new IllegalArgumentException(
                    "As datas de início e fim são obrigatórias."
            );
        }

        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException(
                    "A data de término deve ser posterior à data de início."
            );
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        validarDatas(inicio, this.fim);
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        validarDatas(this.inicio, fim);
        this.fim = fim;
    }

    public boolean isEncerrado() {
        return encerrado;
    }

    public void setEncerrado(boolean encerrado) {
        this.encerrado = encerrado;
    }
}
