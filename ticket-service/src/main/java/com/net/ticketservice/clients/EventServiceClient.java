package com.net.ticketservice.clients;

import com.net.ticketservice.dto.EventDTO;
import com.net.ticketservice.models.Event;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="event-service")
public interface EventServiceClient {
    @GetMapping("api/events/{id}")
    @CircuitBreaker(name = "EventService",fallbackMethod = "getDefaultEvent")
    EventDTO getEventById(@PathVariable Long id);

    default Event getdefaultEvent(Long id){
        Event event =new Event();
        event.setEventId(id);
        event.setDescription("Not Available");
        event.setTitle("Not Available");
        event.setLocation("Not Available");
        return event;
    }

    @PatchMapping("/api/events/{id}/tickets")
    Boolean updateAvailableTickets(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);
}
