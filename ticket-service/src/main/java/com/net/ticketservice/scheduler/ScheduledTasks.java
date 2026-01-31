package com.net.ticketservice.scheduler;

import com.net.ticketservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final ReservationService reservationService;

    @Scheduled(fixedRate = 60000) // Every minute
    public void processExpiredReservations() {
        log.info("Processing expired reservations...");
        reservationService.processExpiredReservations();
    }
}