
package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.Aluno;
import com.example.backend.model.Matricula;
import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.Usuario;

import com.example.backend.repository.MatriculaRepository;
import com.example.backend.repository.MatriculaStatusRepository;
import com.example.backend.repository.OfertaDisciplinaRepository;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.model.enums.TipoMatricula;
import com.example.backend.model.enums.StatusMatricula;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.repository.OfertaVagaRepository;
import com.example.backend.repository.MatriculaConcorrenciaRepository;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final OfertaDisciplinaRepository ofertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PeriodoMatriculaService periodoService;
    private final OfertaVagaRepository ofertaVagaRepository;
    private final MatriculaConcorrenciaRepository concorrenciaRepository;
    private final MatriculaStatusRepository matriculaStatusRepository;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            OfertaDisciplinaRepository ofertaRepository,
            UsuarioRepository usuarioRepository,
            PeriodoMatriculaService periodoService,
            OfertaVagaRepository ofertaVagaRepository,
            MatriculaStatusRepository matriculaStatusRepository,
            MatriculaConcorrenciaRepository concorrenciaRepository) {

        this.matriculaRepository = matriculaRepository;
        this.ofertaRepository = ofertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.periodoService = periodoService;
        this.ofertaVagaRepository = ofertaVagaRepository;
        this.concorrenciaRepository = concorrenciaRepository;
        this.matriculaStatusRepository = matriculaStatusRepository;
    }

    @Transactional
    public Matricula solicitarMatricula(
            String alunoId,
            String ofertaId,
            TipoMatricula tipo) {

        // Validar aluno, oferta e período de matrícula
        validarSolicitacao(alunoId, ofertaId);

        // Registrar a tentativa no documento do aluno
        // antes de consultar os limites de matrícula
        boolean registrada = concorrenciaRepository.registrarTentativa(alunoId);

        if (!registrada) {
            throw new IllegalStateException(
                    "Não foi possível validar a solicitação do aluno.");
        }

        // Verificar duplicidade e limites por semestre
        validarLimites(alunoId, ofertaId, tipo);

        // Reservar uma vaga atomicamente
        boolean reservada = ofertaVagaRepository.reservarVaga(ofertaId);

        if (!reservada) {
            throw new IllegalStateException(
                    "Não foi possível reservar uma vaga nesta oferta.");
        }

        // Criar e salvar a matrícula
        Matricula matricula = new Matricula(
                alunoId,
                ofertaId,
                tipo);

        return matriculaRepository.save(matricula);
    }

    // Listar matrículas de um aluno
    public List<Matricula> listarPorAluno(String alunoId) {
        validarAluno(alunoId);

        return matriculaRepository.findByAlunoId(alunoId);
    }

    // Verificar se o aluno existe
    private Aluno validarAluno(String alunoId) {

        Usuario usuario = usuarioRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aluno não encontrado."));

        if (!(usuario instanceof Aluno aluno)) {
            throw new IllegalArgumentException(
                    "O usuário informado não é um aluno.");
        }

        return aluno;
    }

    // Verificar se a oferta existe
    private OfertaDisciplina validarOferta(String ofertaId) {

        return ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Oferta de disciplina não encontrada."));
    }

    // Validar se o aluno pode solicitar matrícula
    public void validarSolicitacao(
            String alunoId,
            String ofertaId) {

        // Verificar se o usuário é um aluno
        validarAluno(alunoId);

        // Verificar se a oferta existe
        OfertaDisciplina oferta = validarOferta(ofertaId);

        // Verificar se o período de matrícula está aberto
        if (!periodoService.estaAberto(oferta.getSemestre())) {
            throw new IllegalStateException(
                    "O período de matrícula não está aberto.");
        }

        // Verificar se a oferta possui vagas
        if (!oferta.possuiVagas()) {
            throw new IllegalStateException(
                    "A oferta não está aberta ou não possui vagas.");
        }
    }

    // Validar matrículas duplicadas e limites por semestre
    public void validarLimites(
            String alunoId,
            String ofertaId,
            TipoMatricula tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo de matrícula é obrigatório.");
        }

        // Confirmar que o aluno e a oferta existem
        validarAluno(alunoId);
        OfertaDisciplina oferta = validarOferta(ofertaId);

        // Impedir matrícula duplicada na mesma oferta
        boolean jaMatriculado = matriculaRepository.existsByAlunoIdAndOfertaIdAndStatus(
                alunoId,
                ofertaId,
                StatusMatricula.INSCRITA);

        if (jaMatriculado) {
            throw new IllegalStateException(
                    "O aluno já possui matrícula nesta oferta.");
        }

        // Buscar matrículas inscritas do mesmo tipo
        List<Matricula> matriculas = matriculaRepository.findByAlunoIdAndTipoAndStatus(
                alunoId,
                tipo,
                StatusMatricula.INSCRITA);

        int quantidade = 0;

        // Contar apenas matrículas do semestre solicitado
        for (Matricula matricula : matriculas) {

            OfertaDisciplina ofertaExistente = ofertaRepository.findById(matricula.getOfertaId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Uma matrícula possui oferta inexistente."));

            if (ofertaExistente.getSemestre()
                    .equals(oferta.getSemestre())) {
                quantidade++;
            }
        }

        int limite = tipo == TipoMatricula.PRIMEIRA_OPCAO ? 4 : 2;

        if (quantidade >= limite) {
            throw new IllegalStateException(
                    "Limite de " + limite
                            + " matrículas do tipo " + tipo
                            + " atingido para o semestre "
                            + oferta.getSemestre() + ".");
        }
    }

    // Cancelar matrícula e devolver a vaga

    @Transactional
    public Matricula cancelarMatricula(
            String alunoId,
            String matriculaId) {

        // Confirmar que o aluno existe
        validarAluno(alunoId);

        // Buscar a matrícula
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Matrícula não encontrada."));

        // Impedir cancelamento de matrícula de outro aluno
        if (!matricula.getAlunoId().equals(alunoId)) {
            throw new IllegalArgumentException(
                    "Esta matrícula não pertence ao aluno autenticado.");
        }

        // Buscar a oferta
        OfertaDisciplina oferta = validarOferta(
                matricula.getOfertaId());

        // Verificar se o período está aberto
        if (!periodoService.estaAberto(oferta.getSemestre())) {
            throw new IllegalStateException(
                    "O período de matrícula não está aberto para cancelamento.");
        }

        // Registrar a operação concorrente
        boolean registrada = concorrenciaRepository.registrarTentativa(alunoId);

        if (!registrada) {
            throw new IllegalStateException(
                    "Não foi possível validar o cancelamento.");
        }

        // Cancelar atomicamente somente se ainda estiver inscrita
        boolean cancelada = matriculaStatusRepository.cancelarSeInscrita(
                matriculaId,
                alunoId);

        if (!cancelada) {
            throw new IllegalStateException(
                    "A matrícula não está mais inscrita ou já foi cancelada.");
        }

        // Atualizar o objeto em memória
        matricula.cancelar();

        // Devolver a vaga
        boolean devolvida = ofertaVagaRepository.devolverVaga(
                matricula.getOfertaId());

        if (!devolvida) {
            throw new IllegalStateException(
                    "Não foi possível devolver a vaga à oferta.");
        }

        return matricula;
    }

}
