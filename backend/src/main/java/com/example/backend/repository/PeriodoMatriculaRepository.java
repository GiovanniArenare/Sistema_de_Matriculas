
package com.example.backend.repository;

import com.example.backend.model.PeriodoMatricula;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodoMatriculaRepository
        extends MongoRepository<PeriodoMatricula, String> {

    // Buscar período pelo semestre
    Optional<PeriodoMatricula> findBySemestre(String semestre);

    // Verificar se já existe período para o semestre
    boolean existsBySemestre(String semestre);

    // Listar períodos que não foram encerrados manualmente
    List<PeriodoMatricula> findByEncerradoFalse();

    // Listar períodos encerrados manualmente
    List<PeriodoMatricula> findByEncerradoTrue();
}
