package com.net.ticketservice.web;

import com.net.ticketservice.dto.*;
import com.net.ticketservice.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Reservation management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'ADMIN')")
    @Operation(summary = "Create a new reservation ( User, Organizer, Admin )")
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userKeycloakId = jwt.getSubject();
        ReservationDTO reservation = reservationService.createReservation(request, userKeycloakId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ORGANIZER')")
    @Operation(summary = "Confirm a reservation ( Admin, Organizer )")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @Operation(summary = "Cancel a reservation ( User, Admin )")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-reservations")
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'ADMIN')")
    @Operation(summary = "Get user's reservations ( User, Organizer, Admin )")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@AuthenticationPrincipal Jwt jwt) {
        String userKeycloakId = jwt.getSubject();
        List<ReservationDTO> reservations = reservationService.getUserReservations(userKeycloakId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'ORGANIZER', 'ADMIN')")
    @Operation(summary = "Get reservation by ID ( User, Organizer, Admin )")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyAuthority('ORGANIZER', 'ADMIN')")
    @Operation(summary = "Get reservations for an event (ORGANIZER, ADMIN )")
    public ResponseEntity<List<ReservationDTO>> getEventReservations(@PathVariable Long eventId) {
        List<ReservationDTO> reservations = reservationService.getEventReservations(eventId);
        return ResponseEntity.ok(reservations);
    }
}