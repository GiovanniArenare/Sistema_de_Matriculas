
package com.example.backend.repository;

import com.example.backend.model.Matricula;
import com.example.backend.model.enums.StatusMatricula;
import com.example.backend.model.enums.TipoMatricula;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatriculaRepository
        extends MongoRepository<Matricula, String> {

    // Buscar todas as matrículas de um aluno
    List<Matricula> findByAlunoId(String alunoId);

    // Buscar matrículas de uma oferta
    List<Matricula> findByOfertaId(String ofertaId);


    // Buscar matrículas de uma oferta por status
    List<Matricula> findByOfertaIdAndStatus(
            String ofertaId,
            StatusMatricula status
    );


    // Buscar matrículas de um aluno por status
    List<Matricula> findByAlunoIdAndStatus(
            String alunoId,
            StatusMatricula status
    );

    // Buscar matrículas de um aluno por tipo e status
    List<Matricula> findByAlunoIdAndTipoAndStatus(
            String alunoId,
            TipoMatricula tipo,
            StatusMatricula status
    );

    // Verificar se o aluno já possui matrícula inscrita
    // em determinada oferta
    boolean existsByAlunoIdAndOfertaIdAndStatus(
            String alunoId,
            String ofertaId,
            StatusMatricula status
    );

    // Contar matrículas inscritas em uma oferta
    long countByOfertaIdAndStatus(
            String ofertaId,
            StatusMatricula status
    );
}
