package com.net.ticketservice.clients;

import com.net.ticketservice.models.Event;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="event-service")
public interface EventRestClient {
    @GetMapping("api/events/{id}")
    @CircuitBreaker(name = "EventService",fallbackMethod = "getDefaultEvent")
    Event getEventById(@PathVariable Long id);

    default Event getdefaultEvent(Long id){
        Event event =new Event();
        event.setEventId(id);
        event.setDescription("Not Available");
        event.setTitle("Not Available");
        event.setLocation("Not Available");
        return event;
    }
}
