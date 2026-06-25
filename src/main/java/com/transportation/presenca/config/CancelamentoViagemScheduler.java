package com.transportation.presenca.config;

import com.transportation.presenca.model.Presenca;
import com.transportation.presenca.model.StatusPresenca;
import com.transportation.presenca.repository.PresencaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Cancela automaticamente reservas de viagem (status PENDENTE) que não foram
 * efetivadas até a data da viagem — "confirmar até 7 dias antes, senão cancela".
 * Roda diariamente às 02:00.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CancelamentoViagemScheduler {

    private final PresencaRepository presencaRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cancelarReservasNaoEfetivadas() {
        List<Presenca> pendentes = presencaRepository
                .findByStatusAndDataPresencaBefore(StatusPresenca.PENDENTE, LocalDate.now());
        if (pendentes.isEmpty()) {
            return;
        }
        for (Presenca p : pendentes) {
            p.setStatus(StatusPresenca.CANCELADO);
            presencaRepository.save(p);
        }
        log.info("Canceladas {} reserva(s) de viagem não efetivadas", pendentes.size());
    }
}
