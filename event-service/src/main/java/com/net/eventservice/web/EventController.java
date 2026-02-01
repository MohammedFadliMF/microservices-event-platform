package com.net.eventservice.web;

import com.net.eventservice.dto.*;
import com.net.eventservice.entities.Event;
import com.net.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create a new event ( ORGANIZER, ADMIN )")
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String organizerKeycloakId = jwt.getSubject();

        EventDTO event = eventService.createEvent(request, organizerKeycloakId);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Update an event ( ORGANIZER, ADMIN )")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String organizerKeycloakId = jwt.getSubject();
        EventDTO event = eventService.updateEvent(id, request, organizerKeycloakId);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get event by ID ( permitAll )")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get all events ( permitAll )")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/my-events")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    @Operation(summary = "Get events by organizer ( ORGANIZER )")
    public ResponseEntity<List<EventDTO>> getMyEvents(@AuthenticationPrincipal Jwt jwt) {
        String organizerKeycloakId = jwt.getSubject();
        List<EventDTO> events = eventService.getEventsByOrganizer(organizerKeycloakId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/search")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Search events ( permitAll )")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestBody EventSearchRequest searchRequest) {
        List<EventDTO> events = eventService.searchEvents(searchRequest);
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Delete an event ( ORGANIZER, ADMIN )")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        String organizerKeycloakId = jwt.getSubject();
        eventService.deleteEvent(id, organizerKeycloakId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/tickets")
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Update available tickets ( ORGANIZER, ADMIN )")
    public ResponseEntity<Boolean> updateAvailableTickets(
            @PathVariable Long id,
            @RequestParam int quantity) {

        boolean updated = eventService.updateAvailableTickets(id, quantity);
        return ResponseEntity.ok(updated);
    }

//    @GetMapping("/auth")
//    public Authentication authentication(Authentication authentication) {
//        return authentication;
//    }

}