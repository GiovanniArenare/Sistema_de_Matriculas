
package com.example.backend.service;

import com.example.backend.model.Disciplina;
import com.example.backend.repository.DisciplinaRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository) {
        this.disciplinaRepository = disciplinaRepository;
    }

    // Listar todas as disciplinas
    public List<Disciplina> listarTodas() {
        return disciplinaRepository.findAll();
    }

    // Buscar disciplina pelo ID
    public Disciplina buscarPorId(String id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Disciplina não encontrada."
                ));
    }

    // Pesquisar disciplinas pelo nome
    public List<Disciplina> buscarPorNome(String nome) {
        return disciplinaRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Buscar disciplinas pela quantidade de créditos
    public List<Disciplina> buscarPorCreditos(int creditos) {
        return disciplinaRepository.findByCreditos(creditos);
    }

    // Cadastrar disciplina
    public Disciplina cadastrar(Disciplina disciplina) {

        validarDisciplina(disciplina);

        if (disciplinaRepository.existsByCodigoIgnoreCase(
                disciplina.getCodigo())) {

            throw new IllegalArgumentException(
                    "Já existe uma disciplina com este código."
            );
        }

        disciplina.setId(null);

        return disciplinaRepository.save(disciplina);
    }

    // Atualizar disciplina
    public Disciplina atualizar(String id, Disciplina dados) {

        Disciplina disciplina = buscarPorId(id);

        validarDisciplina(dados);

        disciplinaRepository.findByCodigoIgnoreCase(dados.getCodigo())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Já existe outra disciplina com este código."
                        );
                    }
                });

        disciplina.setCodigo(dados.getCodigo());
        disciplina.setNome(dados.getNome());
        disciplina.setCreditos(dados.getCreditos());

        return disciplinaRepository.save(disciplina);
    }

    // Excluir disciplina
    public void excluir(String id) {

        Disciplina disciplina = buscarPorId(id);

        disciplinaRepository.delete(disciplina);
    }

    // Validar dados da disciplina
    private void validarDisciplina(Disciplina disciplina) {

        if (disciplina == null) {
            throw new IllegalArgumentException(
                    "Os dados da disciplina são obrigatórios."
            );
        }

        if (disciplina.getCodigo() == null
                || disciplina.getCodigo().isBlank()) {

            throw new IllegalArgumentException(
                    "O código da disciplina é obrigatório."
            );
        }

        if (disciplina.getNome() == null
                || disciplina.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome da disciplina é obrigatório."
            );
        }

        if (disciplina.getCreditos() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de créditos deve ser maior que zero."
            );
        }

        // Padronizar os dados
        disciplina.setCodigo(
                disciplina.getCodigo().trim().toUpperCase()
        );

        disciplina.setNome(disciplina.getNome().trim());
    }
}
