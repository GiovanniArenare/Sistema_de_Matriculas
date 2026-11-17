
package com.example.backend.service;

import com.example.backend.model.Curso;
import com.example.backend.repository.CursoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    // Listar todos os cursos
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    // Buscar curso pelo ID
    public Curso buscarPorId(String id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Curso não encontrado."
                ));
    }

    // Buscar cursos pelo nome
    public List<Curso> buscarPorNome(String nome) {
        return cursoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Cadastrar curso
    public Curso cadastrar(Curso curso) {

        validarCurso(curso);

        if (cursoRepository.existsByNomeIgnoreCase(curso.getNome())) {
            throw new IllegalArgumentException(
                    "Já existe um curso cadastrado com este nome."
            );
        }

        curso.setId(null);

        return cursoRepository.save(curso);
    }

    // Atualizar curso
    public Curso atualizar(String id, Curso dados) {

        Curso curso = buscarPorId(id);

        validarCurso(dados);

        cursoRepository.findByNomeIgnoreCase(dados.getNome())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Já existe outro curso com este nome."
                        );
                    }
                });

        curso.setNome(dados.getNome().trim());
        curso.setTotalCreditos(dados.getTotalCreditos());

        return cursoRepository.save(curso);
    }

    // Excluir curso
    public void excluir(String id) {

        Curso curso = buscarPorId(id);

        cursoRepository.delete(curso);
    }

    // Validar dados do curso
    private void validarCurso(Curso curso) {

        if (curso == null) {
            throw new IllegalArgumentException(
                    "Os dados do curso são obrigatórios."
            );
        }

        if (curso.getNome() == null || curso.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do curso é obrigatório."
            );
        }

        if (curso.getTotalCreditos() <= 0) {
            throw new IllegalArgumentException(
                    "O total de créditos deve ser maior que zero."
            );
        }

        curso.setNome(curso.getNome().trim());
    }
}
