package com.example.backend.repository;

import com.example.backend.model.Curso;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends MongoRepository<Curso, String> {

    Optional<Curso> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    List<Curso> findByNomeContainingIgnoreCase(String nome);
}