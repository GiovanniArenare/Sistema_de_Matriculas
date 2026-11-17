
package com.example.backend.repository;

import com.example.backend.model.Curriculo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CurriculoRepository
        extends MongoRepository<Curriculo, String> {

    // Buscar currículos de determinado curso
    List<Curriculo> findByCursoId(String cursoId);

    // Buscar currículo pelo curso e semestre
    Optional<Curriculo> findByCursoIdAndSemestre(
            String cursoId,
            String semestre
    );

    // Verificar se já existe currículo para o curso e semestre
    boolean existsByCursoIdAndSemestre(
            String cursoId,
            String semestre
    );

    // Buscar currículos de determinado semestre
    List<Curriculo> findBySemestre(String semestre);
}
