
package com.example.backend.service;

import com.example.backend.model.PeriodoMatricula;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(name = "matriculas.agendador.habilitado", havingValue = "true", matchIfMissing = false)
public class PeriodoMatriculaAgendador {

    private final PeriodoMatriculaService periodoService;
    private static final Logger logger = LoggerFactory.getLogger(PeriodoMatriculaAgendador.class);

    public PeriodoMatriculaAgendador(
            PeriodoMatriculaService periodoService) {

        this.periodoService = periodoService;
    }

    @Scheduled(fixedDelay = 60000)
    public void verificarPeriodosVencidos() {

        verificarPeriodosVencidosEm(LocalDateTime.now());
    }

    public void verificarPeriodosVencidosEm(LocalDateTime agora) {

        List<PeriodoMatricula> periodos = periodoService.listarPeriodosPendentes();

        for (PeriodoMatricula periodo : periodos) {

            if (agora.isAfter(periodo.getFim())) {

                try {

                    periodoService.encerrar(periodo.getId());

                    logger.info(
                            "Período de matrícula encerrado automaticamente: {}",
                            periodo.getId());

                } catch (Exception erro) {

                    logger.error(
                            "Erro ao encerrar automaticamente o período: {}",
                            periodo.getId(),
                            erro);
                }
            }
        }
    }

}
