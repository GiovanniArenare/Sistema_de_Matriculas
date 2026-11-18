
package com.example.backend.service;

import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.PeriodoMatricula;
import com.example.backend.model.enums.StatusOferta;
import com.example.backend.repository.OfertaDisciplinaRepository;
import com.example.backend.repository.PeriodoMatriculaRepository;
import com.example.backend.model.Matricula;
import com.example.backend.model.enums.StatusMatricula;
import com.example.backend.model.enums.TipoMatricula;
import com.example.backend.repository.MatriculaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FinalizacaoPeriodoIntegracaoTest {

    @Autowired
    private PeriodoMatriculaService periodoService;

    @Autowired
    private PeriodoMatriculaRepository periodoRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Test
    void deveCancelarOfertaComMenosDeTresAlunos() {

        String semestre = "TESTE-FINALIZACAO-" + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();
        String ofertaId = null;

        try {

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta.setQuantidadeMatriculados(2);

            oferta = ofertaRepository.save(oferta);
            ofertaId = oferta.getId();

            periodoService.encerrar(periodoId);

            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            PeriodoMatricula periodoAtualizado = periodoRepository
                    .findById(periodoId)
                    .orElseThrow();

            assertEquals(
                    StatusOferta.CANCELADA,
                    atualizada.getStatus());

            assertTrue(periodoAtualizado.isEncerrado());

        } finally {

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
        }
    }

    @Test
    void deveAtivarOfertaComTresAlunos() {

        String semestre = "TESTE-ATIVACAO-" + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();
        String ofertaId = null;

        try {

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta.setQuantidadeMatriculados(3);

            oferta = ofertaRepository.save(oferta);
            ofertaId = oferta.getId();

            // Encerrar o período de matrículas
            periodoService.encerrar(periodoId);

            // Consultar os dados atualizados no MongoDB
            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            PeriodoMatricula periodoAtualizado = periodoRepository
                    .findById(periodoId)
                    .orElseThrow();

            // A oferta deve ficar ativa
            assertEquals(
                    StatusOferta.ATIVA,
                    atualizada.getStatus());

            // O período deve ficar encerrado
            assertTrue(periodoAtualizado.isEncerrado());

            // A quantidade de alunos não deve mudar
            assertEquals(
                    3,
                    atualizada.getQuantidadeMatriculados());

        } finally {

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
        }
    }

    @Test
    void deveAtivarOfertaLotadaComSessentaAlunos() {

        String semestre = "TESTE-LOTADA-" + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();
        String ofertaId = null;

        try {

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta.setQuantidadeMatriculados(60);
            oferta.setStatus(StatusOferta.LOTADA);

            oferta = ofertaRepository.save(oferta);
            ofertaId = oferta.getId();

            // Encerrar o período
            periodoService.encerrar(periodoId);

            // Buscar a oferta atualizada
            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            PeriodoMatricula periodoAtualizado = periodoRepository
                    .findById(periodoId)
                    .orElseThrow();

            // A oferta lotada deve ficar ativa
            assertEquals(
                    StatusOferta.ATIVA,
                    atualizada.getStatus());

            // A quantidade de alunos deve permanecer 60
            assertEquals(
                    60,
                    atualizada.getQuantidadeMatriculados());

            // O período deve estar encerrado
            assertTrue(periodoAtualizado.isEncerrado());

        } finally {

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
        }
    }

    @Test
    void naoDeveEncerrarPeriodoDuasVezes() {

        String semestre = "TESTE-DUPLICADO-" + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();

        try {

            // Primeiro encerramento deve funcionar
            periodoService.encerrar(periodoId);

            // Segundo encerramento deve ser bloqueado
            assertThrows(
                    IllegalStateException.class,
                    () -> periodoService.encerrar(periodoId));

            // O período deve continuar encerrado
            PeriodoMatricula atualizado = periodoRepository
                    .findById(periodoId)
                    .orElseThrow();

            assertTrue(atualizado.isEncerrado());

        } finally {

            periodoRepository.deleteById(periodoId);
        }
    }

    @Test
    void deveIndeferirMatriculasQuandoOfertaForCancelada() {

        String semestre = "TESTE-INDEFERIMENTO-" + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();
        String ofertaId = null;
        String matricula1Id = null;
        String matricula2Id = null;

        try {

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta.setQuantidadeMatriculados(2);
            oferta = ofertaRepository.save(oferta);

            ofertaId = oferta.getId();

            Matricula matricula1 = matriculaRepository.save(
                    new Matricula(
                            "aluno-teste-1",
                            ofertaId,
                            TipoMatricula.PRIMEIRA_OPCAO));

            Matricula matricula2 = matriculaRepository.save(
                    new Matricula(
                            "aluno-teste-2",
                            ofertaId,
                            TipoMatricula.PRIMEIRA_OPCAO));

            matricula1Id = matricula1.getId();
            matricula2Id = matricula2.getId();

            // Secretaria encerra o período
            periodoService.encerrar(periodoId);

            // Consultar os dados atualizados no MongoDB
            OfertaDisciplina ofertaAtualizada = ofertaRepository.findById(ofertaId).orElseThrow();

            Matricula matricula1Atualizada = matriculaRepository.findById(matricula1Id).orElseThrow();

            Matricula matricula2Atualizada = matriculaRepository.findById(matricula2Id).orElseThrow();

            PeriodoMatricula periodoAtualizado = periodoRepository.findById(periodoId).orElseThrow();

            // Verificar os resultados
            assertEquals(
                    StatusOferta.CANCELADA,
                    ofertaAtualizada.getStatus());

            assertEquals(
                    StatusMatricula.INDEFERIDA,
                    matricula1Atualizada.getStatus());

            assertEquals(
                    StatusMatricula.INDEFERIDA,
                    matricula2Atualizada.getStatus());

            assertTrue(periodoAtualizado.isEncerrado());

        } finally {

            if (matricula1Id != null) {
                matriculaRepository.deleteById(matricula1Id);
            }

            if (matricula2Id != null) {
                matriculaRepository.deleteById(matricula2Id);
            }

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
        }
    }

    @Test
    void deveManterMatriculasInscritasQuandoOfertaForAtivada() {

        String semestre = "TESTE-MATRICULAS-ATIVAS-" + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();
        String ofertaId = null;

        List<String> matriculaIds = new ArrayList<>();

        try {

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta.setQuantidadeMatriculados(3);
            oferta = ofertaRepository.save(oferta);

            ofertaId = oferta.getId();

            for (int i = 1; i <= 3; i++) {

                Matricula matricula = matriculaRepository.save(
                        new Matricula(
                                "aluno-teste-" + i,
                                ofertaId,
                                TipoMatricula.PRIMEIRA_OPCAO));

                matriculaIds.add(matricula.getId());
            }

            // Secretaria encerra o período
            periodoService.encerrar(periodoId);

            OfertaDisciplina ofertaAtualizada = ofertaRepository.findById(ofertaId).orElseThrow();

            assertEquals(
                    StatusOferta.ATIVA,
                    ofertaAtualizada.getStatus());

            assertEquals(
                    3,
                    ofertaAtualizada.getQuantidadeMatriculados());

            // Nenhuma matrícula deve ser indeferida
            for (String matriculaId : matriculaIds) {

                Matricula matriculaAtualizada = matriculaRepository.findById(matriculaId)
                        .orElseThrow();

                assertEquals(
                        StatusMatricula.INSCRITA,
                        matriculaAtualizada.getStatus());
            }

            PeriodoMatricula periodoAtualizado = periodoRepository.findById(periodoId).orElseThrow();

            assertTrue(periodoAtualizado.isEncerrado());

        } finally {

            for (String matriculaId : matriculaIds) {
                matriculaRepository.deleteById(matriculaId);
            }

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
        }
    }

    @Test
    void devePreservarMatriculaCanceladaQuandoOfertaForCancelada() {

        String semestre = "TESTE-PRESERVAR-CANCELAMENTO-"
                + UUID.randomUUID();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);

        String periodoId = periodo.getId();
        String ofertaId = null;
        String matriculaInscritaId = null;
        String matriculaCanceladaId = null;

        try {

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta.setQuantidadeMatriculados(1);
            oferta = ofertaRepository.save(oferta);

            ofertaId = oferta.getId();

            // Aluno que continua inscrito
            Matricula matriculaInscrita = matriculaRepository.save(
                    new Matricula(
                            "aluno-teste-1",
                            ofertaId,
                            TipoMatricula.PRIMEIRA_OPCAO));

            matriculaInscritaId = matriculaInscrita.getId();

            // Aluno que já cancelou sua matrícula
            Matricula matriculaCancelada = new Matricula(
                    "aluno-teste-2",
                    ofertaId,
                    TipoMatricula.PRIMEIRA_OPCAO);

            matriculaCancelada.cancelar();

            matriculaCancelada = matriculaRepository.save(matriculaCancelada);

            matriculaCanceladaId = matriculaCancelada.getId();

            // Secretaria encerra o período
            periodoService.encerrar(periodoId);

            OfertaDisciplina ofertaAtualizada = ofertaRepository.findById(ofertaId).orElseThrow();

            Matricula inscritaAtualizada = matriculaRepository.findById(matriculaInscritaId)
                    .orElseThrow();

            Matricula canceladaAtualizada = matriculaRepository.findById(matriculaCanceladaId)
                    .orElseThrow();

            // Oferta deve ser cancelada
            assertEquals(
                    StatusOferta.CANCELADA,
                    ofertaAtualizada.getStatus());

            // Matrícula inscrita deve ser indeferida
            assertEquals(
                    StatusMatricula.INDEFERIDA,
                    inscritaAtualizada.getStatus());

            // Matrícula já cancelada deve permanecer cancelada
            assertEquals(
                    StatusMatricula.CANCELADA,
                    canceladaAtualizada.getStatus());

        } finally {

            if (matriculaInscritaId != null) {
                matriculaRepository.deleteById(matriculaInscritaId);
            }

            if (matriculaCanceladaId != null) {
                matriculaRepository.deleteById(matriculaCanceladaId);
            }

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
        }
    }

}
