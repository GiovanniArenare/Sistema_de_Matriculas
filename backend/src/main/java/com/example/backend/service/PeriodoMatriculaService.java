
package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.PeriodoMatricula;
import com.example.backend.repository.PeriodoMatriculaRepository;
import com.example.backend.repository.OfertaDisciplinaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.Matricula;
import com.example.backend.model.enums.StatusMatricula;
import com.example.backend.model.enums.StatusOferta;
import com.example.backend.repository.MatriculaRepository;

@Service
public class PeriodoMatriculaService {

    private final PeriodoMatriculaRepository repository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final MatriculaRepository matriculaRepository;

    public PeriodoMatriculaService(
            PeriodoMatriculaRepository repository,
            OfertaDisciplinaRepository ofertaRepository,
            MatriculaRepository matriculaRepository) {

        this.repository = repository;
        this.ofertaRepository = ofertaRepository;
        this.matriculaRepository = matriculaRepository;
    }

    // Listar todos os períodos
    public List<PeriodoMatricula> listarTodos() {
        return repository.findAll();
    }

    public List<PeriodoMatricula> listarPeriodosPendentes() {

        return repository.findByEncerradoFalse();
    }

    // Buscar período pelo ID
    public PeriodoMatricula buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Período de matrícula não encontrado."));
    }

    // Buscar período pelo semestre
    public PeriodoMatricula buscarPorSemestre(String semestre) {
        return repository.findBySemestre(semestre)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Não existe período de matrícula para o semestre informado."));
    }

    // Cadastrar novo período
    public PeriodoMatricula cadastrar(
            String semestre,
            LocalDateTime inicio,
            LocalDateTime fim) {

        if (semestre == null || semestre.isBlank()) {
            throw new IllegalArgumentException(
                    "O semestre é obrigatório.");
        }

        semestre = semestre.trim();

        if (repository.existsBySemestre(semestre)) {
            throw new IllegalArgumentException(
                    "Já existe um período de matrícula para este semestre.");
        }

        PeriodoMatricula periodo = new PeriodoMatricula(semestre, inicio, fim);

        return repository.save(periodo);
    }

    // Verificar se o semestre está aberto para matrículas
    public boolean estaAberto(String semestre) {

        PeriodoMatricula periodo = buscarPorSemestre(semestre);

        return periodo.estaAberto(LocalDateTime.now());
    }

    @Transactional
    public PeriodoMatricula encerrar(String id) {

        PeriodoMatricula periodo = buscarPorId(id);

        periodo.encerrar();

        List<OfertaDisciplina> ofertas = ofertaRepository.findBySemestre(periodo.getSemestre());

        for (OfertaDisciplina oferta : ofertas) {

            oferta.finalizarMatriculas();

            if (oferta.getStatus() == StatusOferta.CANCELADA) {

                List<Matricula> matriculas = matriculaRepository.findByOfertaIdAndStatus(
                        oferta.getId(),
                        StatusMatricula.INSCRITA);

                for (Matricula matricula : matriculas) {
                    matricula.indeferir();
                }

                matriculaRepository.saveAll(matriculas);
            }
        }

        ofertaRepository.saveAll(ofertas);

        return repository.save(periodo);
    }

}
