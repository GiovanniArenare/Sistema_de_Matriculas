
package com.example.backend.service;

import com.example.backend.model.Aluno;
import com.example.backend.model.Matricula;
import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.enums.StatusMatricula;
import com.example.backend.model.enums.TipoMatricula;

import com.example.backend.repository.MatriculaRepository;
import com.example.backend.repository.OfertaDisciplinaRepository;
import com.example.backend.repository.UsuarioRepository;
import com.example.backend.repository.OfertaVagaRepository;
import com.example.backend.repository.MatriculaConcorrenciaRepository;
import com.example.backend.repository.MatriculaStatusRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MatriculaServiceTest {

    private MatriculaRepository matriculaRepository;
    private OfertaDisciplinaRepository ofertaRepository;
    private UsuarioRepository usuarioRepository;
    private PeriodoMatriculaService periodoService;
    private OfertaVagaRepository ofertaVagaRepository;
    private MatriculaStatusRepository matriculaStatusRepository;
    private MatriculaConcorrenciaRepository concorrenciaRepository;

    private MatriculaService service;

    private final String alunoId = "aluno1";
    private final String ofertaId = "ofertaNova";

    @BeforeEach
    void configurar() {

        matriculaRepository = mock(MatriculaRepository.class);
        ofertaRepository = mock(OfertaDisciplinaRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        periodoService = mock(PeriodoMatriculaService.class);
        ofertaVagaRepository = mock(OfertaVagaRepository.class);
        matriculaStatusRepository = mock(MatriculaStatusRepository.class);
        concorrenciaRepository = mock(MatriculaConcorrenciaRepository.class);

        service = new MatriculaService(
                matriculaRepository,
                ofertaRepository,
                usuarioRepository,
                periodoService,
                ofertaVagaRepository,
                matriculaStatusRepository,
                concorrenciaRepository
        );

        Aluno aluno = new Aluno();
        aluno.setId(alunoId);

        when(concorrenciaRepository.registrarTentativa(alunoId))
                .thenReturn(true);

        when(usuarioRepository.findById(alunoId))
                .thenReturn(Optional.of(aluno));

        OfertaDisciplina oferta = new OfertaDisciplina(
                "disciplina1",
                "professor1",
                "2027/1"
        );

        oferta.setId(ofertaId);

        when(ofertaRepository.findById(ofertaId))
                .thenReturn(Optional.of(oferta));
    }

    private void configurarMatriculas(
            int quantidade,
            TipoMatricula tipo,
            String semestre) {

        List<Matricula> matriculas = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {

            String idOferta = "ofertaExistente" + i;

            Matricula matricula = new Matricula(
                    alunoId,
                    idOferta,
                    tipo
            );

            matriculas.add(matricula);

            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina" + i,
                    "professor1",
                    semestre
            );

            oferta.setId(idOferta);

            when(ofertaRepository.findById(idOferta))
                    .thenReturn(Optional.of(oferta));
        }

        when(matriculaRepository.findByAlunoIdAndTipoAndStatus(
                alunoId,
                tipo,
                StatusMatricula.INSCRITA
        )).thenReturn(matriculas);
    }

    // =====================================================
    // TESTES DE VALIDAÇÃO DE MATRÍCULA
    // =====================================================

    @Test
    void deveImpedirMatriculaDuplicada() {

        when(matriculaRepository.existsByAlunoIdAndOfertaIdAndStatus(
                alunoId,
                ofertaId,
                StatusMatricula.INSCRITA
        )).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );
    }

    @Test
    void deveBloquearQuintaPrimeiraOpcao() {

        configurarMatriculas(
                4,
                TipoMatricula.PRIMEIRA_OPCAO,
                "2027/1"
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );
    }

    @Test
    void devePermitirQuartaPrimeiraOpcao() {

        configurarMatriculas(
                3,
                TipoMatricula.PRIMEIRA_OPCAO,
                "2027/1"
        );

        assertDoesNotThrow(
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );
    }

    @Test
    void deveBloquearTerceiraAlternativa() {

        configurarMatriculas(
                2,
                TipoMatricula.ALTERNATIVA,
                "2027/1"
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.ALTERNATIVA
                )
        );
    }

    @Test
    void devePermitirSegundaAlternativa() {

        configurarMatriculas(
                1,
                TipoMatricula.ALTERNATIVA,
                "2027/1"
        );

        assertDoesNotThrow(
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.ALTERNATIVA
                )
        );
    }

    @Test
    void deveIgnorarMatriculasDeOutroSemestre() {

        configurarMatriculas(
                4,
                TipoMatricula.PRIMEIRA_OPCAO,
                "2026/2"
        );

        assertDoesNotThrow(
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );
    }

    @Test
    void deveIgnorarMatriculasCanceladasOuIndeferidas() {

        when(matriculaRepository.findByAlunoIdAndTipoAndStatus(
                alunoId,
                TipoMatricula.PRIMEIRA_OPCAO,
                StatusMatricula.INSCRITA
        )).thenReturn(List.of());

        assertDoesNotThrow(
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );
    }

    @Test
    void deveRejeitarTipoNulo() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.validarLimites(
                        alunoId,
                        ofertaId,
                        null
                )
        );
    }

    // =====================================================
    // TESTES DE SOLICITAÇÃO DE MATRÍCULA
    // =====================================================

    @Test
    void deveSolicitarMatriculaComSucesso() {

        when(periodoService.estaAberto("2027/1"))
                .thenReturn(true);

        when(ofertaVagaRepository.reservarVaga(ofertaId))
                .thenReturn(true);

        when(matriculaRepository.save(any(Matricula.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Matricula resultado = service.solicitarMatricula(
                alunoId,
                ofertaId,
                TipoMatricula.PRIMEIRA_OPCAO
        );

        assertNotNull(resultado);
        assertEquals(alunoId, resultado.getAlunoId());
        assertEquals(ofertaId, resultado.getOfertaId());

        assertEquals(
                TipoMatricula.PRIMEIRA_OPCAO,
                resultado.getTipo()
        );

        assertEquals(
                StatusMatricula.INSCRITA,
                resultado.getStatus()
        );

        assertNotNull(resultado.getDataSolicitacao());

        verify(ofertaVagaRepository).reservarVaga(ofertaId);
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void naoDeveSalvarMatriculaQuandoReservaFalhar() {

        when(periodoService.estaAberto("2027/1"))
                .thenReturn(true);

        when(ofertaVagaRepository.reservarVaga(ofertaId))
                .thenReturn(false);

        assertThrows(
                IllegalStateException.class,
                () -> service.solicitarMatricula(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );

        verify(ofertaVagaRepository).reservarVaga(ofertaId);

        verify(matriculaRepository, never())
                .save(any(Matricula.class));
    }

    @Test
    void naoDeveMatricularQuandoControleConcorrenciaFalhar() {

        when(periodoService.estaAberto("2027/1"))
                .thenReturn(true);

        when(concorrenciaRepository.registrarTentativa(alunoId))
                .thenReturn(false);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> service.solicitarMatricula(
                        alunoId,
                        ofertaId,
                        TipoMatricula.PRIMEIRA_OPCAO
                )
        );

        assertEquals(
                "Não foi possível validar a solicitação do aluno.",
                excecao.getMessage()
        );

        verify(concorrenciaRepository)
                .registrarTentativa(alunoId);

        verify(ofertaVagaRepository, never())
                .reservarVaga(anyString());

        verify(matriculaRepository, never())
                .save(any(Matricula.class));
    }

    // =====================================================
    // TESTES DE CANCELAMENTO DE MATRÍCULA
    // =====================================================

    @Test
    void deveCancelarMatriculaEDevolverVaga() {

        String matriculaId = "matricula123";

        Matricula matricula = new Matricula(
                alunoId,
                ofertaId,
                TipoMatricula.PRIMEIRA_OPCAO
        );

        matricula.setId(matriculaId);

        when(matriculaRepository.findById(matriculaId))
                .thenReturn(Optional.of(matricula));

        when(periodoService.estaAberto("2027/1"))
                .thenReturn(true);

        when(matriculaStatusRepository.cancelarSeInscrita(
                matriculaId,
                alunoId
        )).thenReturn(true);

        when(ofertaVagaRepository.devolverVaga(ofertaId))
                .thenReturn(true);

        Matricula resultado = service.cancelarMatricula(
                alunoId,
                matriculaId
        );

        assertNotNull(resultado);

        assertEquals(
                StatusMatricula.CANCELADA,
                resultado.getStatus()
        );

        verify(concorrenciaRepository)
                .registrarTentativa(alunoId);

        verify(matriculaStatusRepository)
                .cancelarSeInscrita(matriculaId, alunoId);

        verify(ofertaVagaRepository)
                .devolverVaga(ofertaId);

        // A alteração já foi realizada atomicamente
        // pelo MatriculaStatusRepository.
        verify(matriculaRepository, never())
                .save(any(Matricula.class));
    }

    @Test
    void naoDeveCancelarMatriculaDuasVezes() {

        String matriculaId = "matriculaCancelada";

        Matricula matricula = new Matricula(
                alunoId,
                ofertaId,
                TipoMatricula.PRIMEIRA_OPCAO
        );

        matricula.setId(matriculaId);

        // Simular matrícula já cancelada
        matricula.cancelar();

        when(periodoService.estaAberto("2027/1"))
                .thenReturn(true);

        when(matriculaRepository.findById(matriculaId))
                .thenReturn(Optional.of(matricula));

        // O MongoDB informa que a matrícula
        // não está mais inscrita.
        when(matriculaStatusRepository.cancelarSeInscrita(
                matriculaId,
                alunoId
        )).thenReturn(false);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> service.cancelarMatricula(
                        alunoId,
                        matriculaId
                )
        );

        assertEquals(
                "A matrícula não está mais inscrita ou já foi cancelada.",
                excecao.getMessage()
        );

        verify(matriculaStatusRepository)
                .cancelarSeInscrita(matriculaId, alunoId);

        // Nenhuma vaga deve ser devolvida
        verify(ofertaVagaRepository, never())
                .devolverVaga(anyString());

        // Nenhuma alteração adicional deve ser salva
        verify(matriculaRepository, never())
                .save(any(Matricula.class));
    }

    @Test
    void naoDeveCancelarMatriculaDeOutroAluno() {

        String matriculaId = "matriculaOutroAluno";
        String outroAlunoId = "outroAluno123";

        // Criar matrícula pertencente a outro aluno
        Matricula matricula = new Matricula(
                outroAlunoId,
                ofertaId,
                TipoMatricula.PRIMEIRA_OPCAO
        );

        matricula.setId(matriculaId);

        when(matriculaRepository.findById(matriculaId))
                .thenReturn(Optional.of(matricula));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelarMatricula(
                        alunoId,
                        matriculaId
                )
        );

        assertEquals(
                "Esta matrícula não pertence ao aluno autenticado.",
                excecao.getMessage()
        );

        // Não deve registrar tentativa de cancelamento
        verify(concorrenciaRepository, never())
                .registrarTentativa(anyString());

        // Não deve tentar cancelar no repositório
        verify(matriculaStatusRepository, never())
                .cancelarSeInscrita(anyString(), anyString());

        // Não deve devolver nenhuma vaga
        verify(ofertaVagaRepository, never())
                .devolverVaga(anyString());

        verify(matriculaRepository, never())
                .save(any(Matricula.class));

        assertEquals(
                StatusMatricula.INSCRITA,
                matricula.getStatus()
        );
    }

    @Test
    void naoDeveSalvarCancelamentoQuandoDevolucaoFalhar() {

        String matriculaId = "matriculaFalha";

        Matricula matricula = new Matricula(
                alunoId,
                ofertaId,
                TipoMatricula.PRIMEIRA_OPCAO
        );

        matricula.setId(matriculaId);

        when(matriculaRepository.findById(matriculaId))
                .thenReturn(Optional.of(matricula));

        when(periodoService.estaAberto("2027/1"))
                .thenReturn(true);

        // Simular cancelamento atômico bem-sucedido
        when(matriculaStatusRepository.cancelarSeInscrita(
                matriculaId,
                alunoId
        )).thenReturn(true);

        // Simular falha na devolução da vaga
        when(ofertaVagaRepository.devolverVaga(ofertaId))
                .thenReturn(false);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> service.cancelarMatricula(
                        alunoId,
                        matriculaId
                )
        );

        assertEquals(
                "Não foi possível devolver a vaga à oferta.",
                excecao.getMessage()
        );

        verify(matriculaStatusRepository)
                .cancelarSeInscrita(matriculaId, alunoId);

        verify(ofertaVagaRepository)
                .devolverVaga(ofertaId);

        // Não deve ocorrer um segundo salvamento
        verify(matriculaRepository, never())
                .save(any(Matricula.class));
    }

    @Test
    void naoDeveCancelarMatriculaForaDoPeriodo() {

        String matriculaId = "matriculaForaDoPeriodo";

        Matricula matricula = new Matricula(
                alunoId,
                ofertaId,
                TipoMatricula.PRIMEIRA_OPCAO
        );

        matricula.setId(matriculaId);

        when(matriculaRepository.findById(matriculaId))
                .thenReturn(Optional.of(matricula));

        // Simular período encerrado
        when(periodoService.estaAberto("2027/1"))
                .thenReturn(false);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> service.cancelarMatricula(
                        alunoId,
                        matriculaId
                )
        );

        assertEquals(
                "O período de matrícula não está aberto para cancelamento.",
                excecao.getMessage()
        );

        verify(concorrenciaRepository, never())
                .registrarTentativa(anyString());

        verify(matriculaStatusRepository, never())
                .cancelarSeInscrita(anyString(), anyString());

        verify(ofertaVagaRepository, never())
                .devolverVaga(anyString());

        verify(matriculaRepository, never())
                .save(any(Matricula.class));

        assertEquals(
                StatusMatricula.INSCRITA,
                matricula.getStatus()
        );
    }
}
