
package com.example.backend.service;

import com.example.backend.model.PeriodoMatricula;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeriodoMatriculaAgendadorTest {

    @Mock
    private PeriodoMatriculaService periodoService;

    @InjectMocks
    private PeriodoMatriculaAgendador agendador;

    @Test
    void deveEncerrarPeriodoVencido() {

        LocalDateTime agora = LocalDateTime.of(
                2026, 11, 17, 12, 0
        );

        PeriodoMatricula periodo = new PeriodoMatricula(
                "TESTE-AGENDADOR",
                agora.minusDays(10),
                agora.minusDays(1)
        );

        periodo.setId("periodo-teste");

        when(periodoService.listarPeriodosPendentes())
                .thenReturn(List.of(periodo));

        agendador.verificarPeriodosVencidosEm(agora);

        verify(periodoService, times(1))
                .encerrar("periodo-teste");
    }


@Test
void naoDeveEncerrarPeriodoQueAindaNaoTerminou() {

    LocalDateTime agora = LocalDateTime.of(
            2026, 11, 17, 12, 0
    );

    PeriodoMatricula periodo = new PeriodoMatricula(
            "TESTE-PERIODO-FUTURO",
            agora.minusDays(1),
            agora.plusDays(8)
    );

    periodo.setId("periodo-futuro");

    when(periodoService.listarPeriodosPendentes())
            .thenReturn(List.of(periodo));

    // Executar a verificação automática
    agendador.verificarPeriodosVencidosEm(agora);

    // O serviço não pode encerrar o período
    verify(periodoService, never())
            .encerrar(anyString());
}


@Test
void naoDeveEncerrarQuandoNaoExistemPeriodosPendentes() {

    LocalDateTime agora = LocalDateTime.of(
            2026, 11, 17, 12, 0
    );

    // Nenhum período pendente no sistema
    when(periodoService.listarPeriodosPendentes())
            .thenReturn(List.of());

    // Executar a verificação
    agendador.verificarPeriodosVencidosEm(agora);

    // Nenhum encerramento deve ser solicitado
    verify(periodoService, never())
            .encerrar(anyString());

    // A consulta deve ter sido realizada
    verify(periodoService, times(1))
            .listarPeriodosPendentes();
}


@Test
void deveContinuarProcessandoAposFalhaEmUmPeriodo() {

    LocalDateTime agora = LocalDateTime.of(
            2026, 11, 17, 12, 0
    );

    PeriodoMatricula periodoA = new PeriodoMatricula(
            "TESTE-FALHA-A",
            agora.minusDays(10),
            agora.minusDays(2)
    );

    periodoA.setId("periodo-a");

    PeriodoMatricula periodoB = new PeriodoMatricula(
            "TESTE-FALHA-B",
            agora.minusDays(10),
            agora.minusDays(1)
    );

    periodoB.setId("periodo-b");

    when(periodoService.listarPeriodosPendentes())
            .thenReturn(List.of(periodoA, periodoB));

    // Simular erro no primeiro período
    doThrow(new IllegalStateException("Erro simulado"))
            .when(periodoService)
            .encerrar("periodo-a");

    // O erro não deve interromper o agendador
    assertDoesNotThrow(
            () -> agendador.verificarPeriodosVencidosEm(agora)
    );

    // O primeiro período foi processado
    verify(periodoService, times(1))
            .encerrar("periodo-a");

    // O segundo também deve ser processado
    verify(periodoService, times(1))
            .encerrar("periodo-b");

    // Confirmar a ordem das tentativas
    InOrder ordem = inOrder(periodoService);

    ordem.verify(periodoService).encerrar("periodo-a");
    ordem.verify(periodoService).encerrar("periodo-b");
}



}
