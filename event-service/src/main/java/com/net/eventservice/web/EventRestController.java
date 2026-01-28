package com.net.eventservice.web;

import com.net.eventservice.entities.Event;
import com.net.eventservice.repository.EventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {
    private EventRepository eventRepository;
    public EventRestController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    @GetMapping
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public List<Event> countEvents() {
        return eventRepository.findAll();
    }
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).get();
    }
    @GetMapping("/auth")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }
}
