
package com.example.backend.repository;

import com.example.backend.model.Disciplina;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DisciplinaRepository
        extends MongoRepository<Disciplina, String> {

    // Buscar disciplina pelo código
    Optional<Disciplina> findByCodigoIgnoreCase(String codigo);

    // Verificar se o código já está cadastrado
    boolean existsByCodigoIgnoreCase(String codigo);

    // Pesquisar disciplinas pelo nome
    List<Disciplina> findByNomeContainingIgnoreCase(String nome);

    // Buscar disciplinas por quantidade de créditos
    List<Disciplina> findByCreditos(int creditos);
}
