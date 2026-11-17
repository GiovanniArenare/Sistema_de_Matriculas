
package com.example.backend.service;

import com.example.backend.model.Curriculo;
import com.example.backend.repository.CursoRepository;
import com.example.backend.repository.CurriculoRepository;
import com.example.backend.repository.DisciplinaRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurriculoService {

    private final CurriculoRepository curriculoRepository;
    private final CursoRepository cursoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public CurriculoService(
            CurriculoRepository curriculoRepository,
            CursoRepository cursoRepository,
            DisciplinaRepository disciplinaRepository) {

        this.curriculoRepository = curriculoRepository;
        this.cursoRepository = cursoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    // Listar todos os currículos
    public List<Curriculo> listarTodos() {
        return curriculoRepository.findAll();
    }

    // Buscar currículo pelo ID
    public Curriculo buscarPorId(String id) {
        return curriculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currículo não encontrado."
                ));
    }

    // Buscar currículos de um curso
    public List<Curriculo> buscarPorCurso(String cursoId) {
        return curriculoRepository.findByCursoId(cursoId);
    }

    // Buscar currículo pelo curso e semestre
    public Curriculo buscarPorCursoESemestre(
            String cursoId,
            String semestre) {

        return curriculoRepository
                .findByCursoIdAndSemestre(cursoId, semestre)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currículo não encontrado para este curso e semestre."
                ));
    }

    // Cadastrar currículo
    public Curriculo cadastrar(String cursoId, String semestre) {

        if (cursoId == null || cursoId.isBlank()) {
            throw new IllegalArgumentException(
                    "O curso é obrigatório."
            );
        }

        if (semestre == null || semestre.isBlank()) {
            throw new IllegalArgumentException(
                    "O semestre é obrigatório."
            );
        }

        cursoId = cursoId.trim();
        semestre = semestre.trim();

        // Verificar se o curso existe
        if (!cursoRepository.existsById(cursoId)) {
            throw new IllegalArgumentException(
                    "Curso não encontrado."
            );
        }

        // Impedir currículo duplicado
        if (curriculoRepository.existsByCursoIdAndSemestre(
                cursoId, semestre)) {

            throw new IllegalArgumentException(
                    "Já existe um currículo para este curso e semestre."
            );
        }

        Curriculo curriculo = new Curriculo();
        curriculo.setCursoId(cursoId);
        curriculo.setSemestre(semestre);

        return curriculoRepository.save(curriculo);
    }

    // Adicionar uma disciplina existente ao currículo
    public Curriculo adicionarDisciplina(
            String curriculoId,
            String disciplinaId) {

        Curriculo curriculo = buscarPorId(curriculoId);

        if (disciplinaId == null || disciplinaId.isBlank()) {
            throw new IllegalArgumentException(
                    "O ID da disciplina é obrigatório."
            );
        }

        disciplinaId = disciplinaId.trim();

        // Verificar se a disciplina existe
        if (!disciplinaRepository.existsById(disciplinaId)) {
            throw new IllegalArgumentException(
                    "Disciplina não encontrada."
            );
        }

        // Aplicar a regra de negócio definida no modelo
        curriculo.adicionarDisciplina(disciplinaId);

        return curriculoRepository.save(curriculo);
    }

    // Remover uma disciplina do currículo
    public Curriculo removerDisciplina(
            String curriculoId,
            String disciplinaId) {

        Curriculo curriculo = buscarPorId(curriculoId);

        // Aplicar a regra de negócio definida no modelo
        curriculo.removerDisciplina(disciplinaId);

        return curriculoRepository.save(curriculo);
    }
}
