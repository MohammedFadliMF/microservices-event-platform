package com.net.paymentservice.clients;

import com.net.paymentservice.models.Reservation;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name= "TICKER-SERVICE")
public interface TicketRestClient {
    @GetMapping("/api/tickets/reservation/{id}")
    @CircuitBreaker(name = "ReservationService",fallbackMethod = "getDefaultReservation")
    Reservation findReservationById(@PathVariable Long id);
    default Reservation getDefaultReservation(Long id,Exception exception){
        Reservation reservation=new Reservation();
        reservation.setReservationId(id);
        reservation.setStatus("Not Available");
        return reservation;
    }
}
