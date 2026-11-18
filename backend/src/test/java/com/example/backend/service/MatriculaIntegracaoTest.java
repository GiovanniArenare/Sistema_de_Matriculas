
package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.backend.model.Aluno;
import com.example.backend.model.Matricula;
import com.example.backend.model.OfertaDisciplina;
import com.example.backend.model.PeriodoMatricula;
import com.example.backend.model.enums.PerfilUsuario;
import com.example.backend.model.enums.StatusMatricula;
import com.example.backend.model.enums.TipoMatricula;
import com.example.backend.repository.MatriculaRepository;
import com.example.backend.repository.OfertaDisciplinaRepository;
import com.example.backend.repository.PeriodoMatriculaRepository;
import com.example.backend.repository.UsuarioRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MatriculaIntegracaoTest {

    @Autowired
    private MatriculaService matriculaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OfertaDisciplinaRepository ofertaRepository;

    @Autowired
    private PeriodoMatriculaRepository periodoRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Test
    void deveSolicitarMatriculaRealComSucesso() {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-" + identificador;

        Aluno aluno = new Aluno(
                null,
                "Aluno Teste",
                "teste-" + identificador + "@exemplo.com",
                "senha-teste",
                "MAT-" + identificador);

        aluno.setPerfil(PerfilUsuario.ALUNO);

        aluno = usuarioRepository.save(aluno);
        String alunoId = aluno.getId();

        OfertaDisciplina oferta = new OfertaDisciplina(
                "disciplina-teste",
                "professor-teste",
                semestre);

        oferta = ofertaRepository.save(oferta);
        String ofertaId = oferta.getId();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);
        String periodoId = periodo.getId();

        try {

            Matricula matricula = matriculaService.solicitarMatricula(
                    alunoId,
                    ofertaId,
                    TipoMatricula.PRIMEIRA_OPCAO);

            assertNotNull(matricula.getId());

            Matricula salva = matriculaRepository
                    .findById(matricula.getId())
                    .orElseThrow();

            assertEquals(alunoId, salva.getAlunoId());
            assertEquals(ofertaId, salva.getOfertaId());

            assertEquals(
                    StatusMatricula.INSCRITA,
                    salva.getStatus());

            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            assertEquals(
                    1,
                    atualizada.getQuantidadeMatriculados());

        } finally {

            // Remover somente os dados criados pelo teste
            matriculaRepository.findByAlunoId(alunoId)
                    .forEach(matriculaRepository::delete);

            ofertaRepository.deleteById(ofertaId);
            periodoRepository.deleteById(periodoId);
            usuarioRepository.deleteById(alunoId);
        }
    }

    @Test
    void naoDevePermitirMatriculaDuplicada() {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-DUP-" + identificador;

        Aluno aluno = new Aluno(
                null,
                "Aluno Teste Duplicidade",
                "duplicado-" + identificador + "@exemplo.com",
                "senha-teste",
                "MAT-" + identificador);

        aluno.setPerfil(PerfilUsuario.ALUNO);

        aluno = usuarioRepository.save(aluno);
        String alunoId = aluno.getId();

        OfertaDisciplina oferta = new OfertaDisciplina(
                "disciplina-teste",
                "professor-teste",
                semestre);

        oferta = ofertaRepository.save(oferta);
        String ofertaId = oferta.getId();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);
        String periodoId = periodo.getId();

        try {

            // Primeira solicitação: deve funcionar
            Matricula primeira = matriculaService.solicitarMatricula(
                    alunoId,
                    ofertaId,
                    TipoMatricula.PRIMEIRA_OPCAO);

            assertNotNull(primeira.getId());

            // Segunda solicitação: deve ser rejeitada
            assertThrows(
                    IllegalStateException.class,
                    () -> matriculaService.solicitarMatricula(
                            alunoId,
                            ofertaId,
                            TipoMatricula.PRIMEIRA_OPCAO));

            // Confirmar que existe apenas uma matrícula ativa
            long quantidade = matriculaRepository
                    .findByAlunoId(alunoId)
                    .stream()
                    .filter(m -> m.getStatus() == StatusMatricula.INSCRITA)
                    .filter(m -> ofertaId.equals(m.getOfertaId()))
                    .count();

            assertEquals(1, quantidade);

            // Confirmar que a vaga não foi contabilizada duas vezes
            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            assertEquals(
                    1,
                    atualizada.getQuantidadeMatriculados());

        } finally {

            // Limpar somente os registros temporários
            matriculaRepository.findByAlunoId(alunoId)
                    .forEach(matriculaRepository::delete);

            ofertaRepository.deleteById(ofertaId);
            periodoRepository.deleteById(periodoId);
            usuarioRepository.deleteById(alunoId);
        }
    }

    @Test
    void naoDevePermitirMaisDeQuatroMatriculasPrimeiraOpcao() {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-LIMITE-" + identificador;

        Aluno aluno = new Aluno(
                null,
                "Aluno Teste Limite",
                "limite-" + identificador + "@exemplo.com",
                "senha-teste",
                "MAT-" + identificador);

        aluno.setPerfil(PerfilUsuario.ALUNO);

        aluno = usuarioRepository.save(aluno);
        String alunoId = aluno.getId();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);
        String periodoId = periodo.getId();

        java.util.List<String> ofertasIds = new java.util.ArrayList<>();

        try {

            // Criar 5 ofertas diferentes no mesmo semestre
            for (int i = 1; i <= 5; i++) {

                OfertaDisciplina oferta = new OfertaDisciplina(
                        "disciplina-teste-" + i,
                        "professor-teste",
                        semestre);

                oferta = ofertaRepository.save(oferta);
                ofertasIds.add(oferta.getId());
            }

            // As primeiras 4 matrículas devem funcionar
            for (int i = 0; i < 4; i++) {

                Matricula matricula = matriculaService.solicitarMatricula(
                        alunoId,
                        ofertasIds.get(i),
                        TipoMatricula.PRIMEIRA_OPCAO);

                assertNotNull(matricula.getId());
            }

            // A quinta matrícula deve ser rejeitada
            assertThrows(
                    IllegalStateException.class,
                    () -> matriculaService.solicitarMatricula(
                            alunoId,
                            ofertasIds.get(4),
                            TipoMatricula.PRIMEIRA_OPCAO));

            // Confirmar que existem somente 4 matrículas ativas
            long quantidade = matriculaRepository
                    .findByAlunoId(alunoId)
                    .stream()
                    .filter(m -> m.getStatus() == StatusMatricula.INSCRITA)
                    .filter(m -> m.getTipo() == TipoMatricula.PRIMEIRA_OPCAO)
                    .count();

            assertEquals(4, quantidade);

            // A quinta oferta não deve ter vagas ocupadas
            OfertaDisciplina quintaOferta = ofertaRepository
                    .findById(ofertasIds.get(4))
                    .orElseThrow();

            assertEquals(
                    0,
                    quintaOferta.getQuantidadeMatriculados());

        } finally {

            // Limpar os dados temporários
            matriculaRepository.findByAlunoId(alunoId)
                    .forEach(matriculaRepository::delete);

            for (String ofertaId : ofertasIds) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
            usuarioRepository.deleteById(alunoId);
        }
    }

    @Test
    void naoDevePermitirMaisDeDuasMatriculasAlternativas() {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-ALT-" + identificador;

        Aluno aluno = new Aluno(
                null,
                "Aluno Teste Alternativas",
                "alternativas-" + identificador + "@exemplo.com",
                "senha-teste",
                "MAT-" + identificador);

        aluno.setPerfil(PerfilUsuario.ALUNO);

        aluno = usuarioRepository.save(aluno);
        String alunoId = aluno.getId();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);
        String periodoId = periodo.getId();

        java.util.List<String> ofertasIds = new java.util.ArrayList<>();

        try {

            // Criar 3 ofertas diferentes
            for (int i = 1; i <= 3; i++) {

                OfertaDisciplina oferta = new OfertaDisciplina(
                        "disciplina-alternativa-" + i,
                        "professor-teste",
                        semestre);

                oferta = ofertaRepository.save(oferta);
                ofertasIds.add(oferta.getId());
            }

            // As duas primeiras matrículas devem funcionar
            for (int i = 0; i < 2; i++) {

                Matricula matricula = matriculaService.solicitarMatricula(
                        alunoId,
                        ofertasIds.get(i),
                        TipoMatricula.ALTERNATIVA);

                assertNotNull(matricula.getId());
            }

            // A terceira deve ser rejeitada
            assertThrows(
                    IllegalStateException.class,
                    () -> matriculaService.solicitarMatricula(
                            alunoId,
                            ofertasIds.get(2),
                            TipoMatricula.ALTERNATIVA));

            // Confirmar apenas duas matrículas alternativas ativas
            long quantidade = matriculaRepository
                    .findByAlunoId(alunoId)
                    .stream()
                    .filter(m -> m.getStatus() == StatusMatricula.INSCRITA)
                    .filter(m -> m.getTipo() == TipoMatricula.ALTERNATIVA)
                    .count();

            assertEquals(2, quantidade);

            // A terceira oferta não deve ter vagas ocupadas
            OfertaDisciplina terceiraOferta = ofertaRepository
                    .findById(ofertasIds.get(2))
                    .orElseThrow();

            assertEquals(
                    0,
                    terceiraOferta.getQuantidadeMatriculados());

        } finally {

            // Remover os dados temporários
            matriculaRepository.findByAlunoId(alunoId)
                    .forEach(matriculaRepository::delete);

            for (String ofertaId : ofertasIds) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
            usuarioRepository.deleteById(alunoId);
        }
    }

    @Test
    void devePermitirMatriculasEmSemestresDiferentes() {

        String identificador = UUID.randomUUID().toString();

        String semestre1 = "TESTE-SEM1-" + identificador;
        String semestre2 = "TESTE-SEM2-" + identificador;

        Aluno aluno = new Aluno(
                null,
                "Aluno Teste Semestres",
                "semestres-" + identificador + "@exemplo.com",
                "senha-teste",
                "MAT-" + identificador);

        aluno.setPerfil(PerfilUsuario.ALUNO);

        aluno = usuarioRepository.save(aluno);
        String alunoId = aluno.getId();

        java.util.List<String> ofertasIds = new java.util.ArrayList<>();

        java.util.List<String> periodosIds = new java.util.ArrayList<>();

        try {

            // Criar períodos abertos para os dois semestres
            for (String semestre : java.util.List.of(
                    semestre1, semestre2)) {

                PeriodoMatricula periodo = new PeriodoMatricula(
                        semestre,
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(1));

                periodo = periodoRepository.save(periodo);
                periodosIds.add(periodo.getId());
            }

            // Criar e solicitar 4 matrículas no primeiro semestre
            for (int i = 1; i <= 4; i++) {

                OfertaDisciplina oferta = new OfertaDisciplina(
                        "disciplina-sem1-" + i,
                        "professor-teste",
                        semestre1);

                oferta = ofertaRepository.save(oferta);
                ofertasIds.add(oferta.getId());

                Matricula matricula = matriculaService.solicitarMatricula(
                        alunoId,
                        oferta.getId(),
                        TipoMatricula.PRIMEIRA_OPCAO);

                assertNotNull(matricula.getId());
            }

            // Criar uma oferta no segundo semestre
            OfertaDisciplina novaOferta = new OfertaDisciplina(
                    "disciplina-sem2",
                    "professor-teste",
                    semestre2);

            novaOferta = ofertaRepository.save(novaOferta);
            ofertasIds.add(novaOferta.getId());

            // A matrícula no segundo semestre deve funcionar
            Matricula novaMatricula = matriculaService.solicitarMatricula(
                    alunoId,
                    novaOferta.getId(),
                    TipoMatricula.PRIMEIRA_OPCAO);

            assertNotNull(novaMatricula.getId());

            // Confirmar as 5 matrículas no total
            long quantidade = matriculaRepository
                    .findByAlunoId(alunoId)
                    .stream()
                    .filter(m -> m.getStatus() == StatusMatricula.INSCRITA)
                    .filter(m -> m.getTipo() == TipoMatricula.PRIMEIRA_OPCAO)
                    .count();

            assertEquals(5, quantidade);

            // Confirmar uma vaga ocupada no segundo semestre
            OfertaDisciplina atualizada = ofertaRepository
                    .findById(novaOferta.getId())
                    .orElseThrow();

            assertEquals(
                    1,
                    atualizada.getQuantidadeMatriculados());

        } finally {

            // Remover somente os dados temporários
            matriculaRepository.findByAlunoId(alunoId)
                    .forEach(matriculaRepository::delete);

            for (String ofertaId : ofertasIds) {
                ofertaRepository.deleteById(ofertaId);
            }

            for (String periodoId : periodosIds) {
                periodoRepository.deleteById(periodoId);
            }

            usuarioRepository.deleteById(alunoId);
        }
    }

    @Test
    void naoDeveUltrapassarLimiteComSolicitacoesSimultaneas()
            throws Exception {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-CONCORRENCIA-" + identificador;

        Aluno aluno = new Aluno(
                null,
                "Aluno Teste Concorrencia",
                "concorrencia-" + identificador + "@exemplo.com",
                "senha-teste",
                "MAT-" + identificador);

        aluno.setPerfil(PerfilUsuario.ALUNO);
        aluno = usuarioRepository.save(aluno);

        String alunoId = aluno.getId();

        java.util.List<String> ofertasIds = new java.util.ArrayList<>();

        PeriodoMatricula periodo = new PeriodoMatricula(
                semestre,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1));

        periodo = periodoRepository.save(periodo);
        String periodoId = periodo.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {

            // Criar cinco ofertas
            for (int i = 1; i <= 5; i++) {

                OfertaDisciplina oferta = new OfertaDisciplina(
                        "disciplina-concorrencia-" + i,
                        "professor-teste",
                        semestre);

                oferta = ofertaRepository.save(oferta);
                ofertasIds.add(oferta.getId());
            }

            // Preencher três das quatro matrículas permitidas
            for (int i = 0; i < 3; i++) {

                matriculaService.solicitarMatricula(
                        alunoId,
                        ofertasIds.get(i),
                        TipoMatricula.PRIMEIRA_OPCAO);
            }

            CountDownLatch inicio = new CountDownLatch(1);

            // Duas solicitações simultâneas
            Future<Boolean> primeira = executor.submit(() -> {

                inicio.await();

                try {
                    matriculaService.solicitarMatricula(
                            alunoId,
                            ofertasIds.get(3),
                            TipoMatricula.PRIMEIRA_OPCAO);

                    return true;

                } catch (RuntimeException erro) {
                    return false;
                }
            });

            Future<Boolean> segunda = executor.submit(() -> {

                inicio.await();

                try {
                    matriculaService.solicitarMatricula(
                            alunoId,
                            ofertasIds.get(4),
                            TipoMatricula.PRIMEIRA_OPCAO);

                    return true;

                } catch (RuntimeException erro) {
                    return false;
                }
            });

            // Liberar as duas solicitações
            inicio.countDown();

            boolean sucessoPrimeira = primeira.get(30, TimeUnit.SECONDS);

            boolean sucessoSegunda = segunda.get(30, TimeUnit.SECONDS);

            // No máximo uma nova matrícula pode ser aceita
            int sucessos = (sucessoPrimeira ? 1 : 0)
                    + (sucessoSegunda ? 1 : 0);

            assertTrue(
                    sucessos <= 1,
                    "Duas solicitações simultâneas foram aceitas.");

            // Confirmar o limite no banco
            long quantidade = matriculaRepository
                    .findByAlunoId(alunoId)
                    .stream()
                    .filter(m -> m.getStatus() == StatusMatricula.INSCRITA)
                    .filter(m -> m.getTipo() == TipoMatricula.PRIMEIRA_OPCAO)
                    .count();

            assertEquals(3 + sucessos, quantidade);
            assertTrue(quantidade <= 4);

            // Verificar se a contagem das ofertas está consistente
            int totalVagasOcupadas = 0;

            for (String ofertaId : ofertasIds) {

                OfertaDisciplina oferta = ofertaRepository
                        .findById(ofertaId)
                        .orElseThrow();

                totalVagasOcupadas += oferta.getQuantidadeMatriculados();
            }

            assertEquals(quantidade, totalVagasOcupadas);

        } finally {

            executor.shutdownNow();

            matriculaRepository.findByAlunoId(alunoId)
                    .forEach(matriculaRepository::delete);

            for (String ofertaId : ofertasIds) {
                ofertaRepository.deleteById(ofertaId);
            }

            periodoRepository.deleteById(periodoId);
            usuarioRepository.deleteById(alunoId);
        }
    }

    @Test
    void deveCancelarMatriculaRealEDevolverVaga() {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-CANCELAMENTO-" + identificador;

        String alunoId = null;
        String ofertaId = null;
        String periodoId = null;

        try {

            // Criar aluno temporário
            Aluno aluno = new Aluno(
                    null,
                    "Aluno Teste Cancelamento",
                    "cancelamento-" + identificador + "@exemplo.com",
                    "senha-teste",
                    "MAT-" + identificador);

            aluno.setPerfil(PerfilUsuario.ALUNO);
            aluno = usuarioRepository.save(aluno);
            alunoId = aluno.getId();

            // Criar oferta temporária
            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-teste",
                    "professor-teste",
                    semestre);

            oferta = ofertaRepository.save(oferta);
            ofertaId = oferta.getId();

            // Criar período aberto
            PeriodoMatricula periodo = new PeriodoMatricula(
                    semestre,
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now().plusDays(1));

            periodo = periodoRepository.save(periodo);
            periodoId = periodo.getId();

            // Solicitar matrícula
            Matricula matricula = matriculaService.solicitarMatricula(
                    alunoId,
                    ofertaId,
                    TipoMatricula.PRIMEIRA_OPCAO);

            assertEquals(
                    StatusMatricula.INSCRITA,
                    matricula.getStatus());

            // Confirmar que a vaga foi ocupada
            OfertaDisciplina antes = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            assertEquals(
                    1,
                    antes.getQuantidadeMatriculados());

            // Cancelar matrícula
            Matricula cancelada = matriculaService.cancelarMatricula(
                    alunoId,
                    matricula.getId());

            assertEquals(
                    StatusMatricula.CANCELADA,
                    cancelada.getStatus());

            // Consultar novamente a matrícula no MongoDB
            Matricula salva = matriculaRepository
                    .findById(matricula.getId())
                    .orElseThrow();

            assertEquals(
                    StatusMatricula.CANCELADA,
                    salva.getStatus());

            // Consultar novamente a oferta no MongoDB
            OfertaDisciplina atualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            assertEquals(
                    0,
                    atualizada.getQuantidadeMatriculados());

        } finally {

            // Limpar somente os registros criados pelo teste
            if (alunoId != null) {
                matriculaRepository.findByAlunoId(alunoId)
                        .forEach(matriculaRepository::delete);
            }

            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            if (periodoId != null) {
                periodoRepository.deleteById(periodoId);
            }

            if (alunoId != null) {
                usuarioRepository.deleteById(alunoId);
            }
        }
    }

    @Test
    void naoDeveCancelarMesmaMatriculaSimultaneamente()
            throws Exception {

        String identificador = UUID.randomUUID().toString();
        String semestre = "TESTE-CANCELAMENTO-CONCORRENCIA-" + identificador;

        String alunoId = null;
        String ofertaId = null;
        String periodoId = null;

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {

            // Criar aluno temporário
            Aluno aluno = new Aluno(
                    null,
                    "Aluno Teste Cancelamento Concorrente",
                    "cancelamento-concorrencia-" + identificador + "@exemplo.com",
                    "senha-teste",
                    "MAT-" + identificador);

            aluno.setPerfil(PerfilUsuario.ALUNO);

            aluno = usuarioRepository.save(aluno);
            alunoId = aluno.getId();

            // Criar oferta temporária
            OfertaDisciplina oferta = new OfertaDisciplina(
                    "disciplina-concorrencia",
                    "professor-teste",
                    semestre);

            oferta = ofertaRepository.save(oferta);
            ofertaId = oferta.getId();

            // Criar período aberto
            PeriodoMatricula periodo = new PeriodoMatricula(
                    semestre,
                    LocalDateTime.now().minusDays(1),
                    LocalDateTime.now().plusDays(1));

            periodo = periodoRepository.save(periodo);
            periodoId = periodo.getId();

            // Solicitar matrícula
            Matricula matricula = matriculaService.solicitarMatricula(
                    alunoId,
                    ofertaId,
                    TipoMatricula.PRIMEIRA_OPCAO);

            String matriculaId = matricula.getId();

            // Confirmar estado inicial
            OfertaDisciplina antes = ofertaRepository.findById(ofertaId)
                    .orElseThrow();

            assertEquals(
                    1,
                    antes.getQuantidadeMatriculados());

            assertEquals(
                    StatusMatricula.INSCRITA,
                    matricula.getStatus());

            // Identificadores finais para as threads
            final String alunoIdFinal = alunoId;
            final String matriculaIdFinal = matriculaId;

            // Sincronizar o início das duas requisições
            CountDownLatch inicio = new CountDownLatch(1);

            // Primeira tentativa de cancelamento
            Future<Boolean> primeira = executor.submit(() -> {

                inicio.await();

                try {

                    matriculaService.cancelarMatricula(
                            alunoIdFinal,
                            matriculaIdFinal);

                    return true;

                } catch (RuntimeException erro) {

                    return false;
                }
            });

            // Segunda tentativa de cancelamento
            Future<Boolean> segunda = executor.submit(() -> {

                inicio.await();

                try {

                    matriculaService.cancelarMatricula(
                            alunoIdFinal,
                            matriculaIdFinal);

                    return true;

                } catch (RuntimeException erro) {

                    return false;
                }
            });

            // Liberar as duas requisições simultaneamente
            inicio.countDown();

            boolean sucessoPrimeira = primeira.get(30, TimeUnit.SECONDS);
            boolean sucessoSegunda = segunda.get(30, TimeUnit.SECONDS);

            int sucessos = (sucessoPrimeira ? 1 : 0)
                    + (sucessoSegunda ? 1 : 0);

            // Somente uma requisição pode cancelar
            assertEquals(
                    1,
                    sucessos,
                    "A mesma matrícula foi cancelada mais de uma vez ou nenhuma requisição conseguiu cancelar.");

            // Consultar matrícula diretamente no MongoDB
            Matricula atualizada = matriculaRepository
                    .findById(matriculaId)
                    .orElseThrow();

            assertEquals(
                    StatusMatricula.CANCELADA,
                    atualizada.getStatus());

            // Consultar oferta diretamente no MongoDB
            OfertaDisciplina ofertaAtualizada = ofertaRepository
                    .findById(ofertaId)
                    .orElseThrow();

            // A vaga deve ter sido devolvida exatamente uma vez
            assertEquals(
                    0,
                    ofertaAtualizada.getQuantidadeMatriculados());

        } finally {

            executor.shutdownNow();

            // Remover matrículas temporárias
            if (alunoId != null) {

                matriculaRepository.findByAlunoId(alunoId)
                        .forEach(matriculaRepository::delete);
            }

            // Remover oferta temporária
            if (ofertaId != null) {
                ofertaRepository.deleteById(ofertaId);
            }

            // Remover período temporário
            if (periodoId != null) {
                periodoRepository.deleteById(periodoId);
            }

            // Remover aluno temporário
            if (alunoId != null) {
                usuarioRepository.deleteById(alunoId);
            }
        }
    }

}
