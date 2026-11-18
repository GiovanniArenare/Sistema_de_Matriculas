
package com.example.backend.model;

import java.time.LocalDateTime;

import com.example.backend.model.enums.TipoMatricula;
import com.example.backend.model.enums.StatusMatricula;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "matriculas")
public class Matricula {

    @Id
    private String id;

    private String alunoId;
    private String ofertaId;

    private TipoMatricula tipo;
    private StatusMatricula status;

    private LocalDateTime dataSolicitacao;

    public Matricula() {
    }

    public Matricula(
            String alunoId,
            String ofertaId,
            TipoMatricula tipo) {

        this.alunoId = alunoId;
        this.ofertaId = ofertaId;
        this.tipo = tipo;

        this.status = StatusMatricula.INSCRITA;
        this.dataSolicitacao = LocalDateTime.now();
    }

    // Cancelar uma matrícula
    public void cancelar() {

        if (status != StatusMatricula.INSCRITA) {
            throw new IllegalStateException(
                    "Somente matrículas inscritas podem ser canceladas."
            );
        }

        status = StatusMatricula.CANCELADA;
    }


    // Indeferir matrícula quando a oferta não for ativada
    public void indeferir() {

        if (status != StatusMatricula.INSCRITA) {
            throw new IllegalStateException(
                    "Somente matrículas inscritas podem ser indeferidas."
            );
        }

        status = StatusMatricula.INDEFERIDA;
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

    public String getOfertaId() {
        return ofertaId;
    }

    public void setOfertaId(String ofertaId) {
        this.ofertaId = ofertaId;
    }

    public TipoMatricula getTipo() {
        return tipo;
    }

    public void setTipo(TipoMatricula tipo) {
        this.tipo = tipo;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(
            LocalDateTime dataSolicitacao) {

        this.dataSolicitacao = dataSolicitacao;
    }
}
