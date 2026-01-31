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
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userKeycloakId = jwt.getSubject();
        ReservationDTO reservation = reservationService.createReservation(request, userKeycloakId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm a reservation")
    public ResponseEntity<ReservationDTO> confirmReservation(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.confirmReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "Get user's reservations")
    public ResponseEntity<List<ReservationDTO>> getUserReservations(@AuthenticationPrincipal Jwt jwt) {
        String userKeycloakId = jwt.getSubject();
        List<ReservationDTO> reservations = reservationService.getUserReservations(userKeycloakId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get reservations for an event")
    public ResponseEntity<List<ReservationDTO>> getEventReservations(@PathVariable Long eventId) {
        List<ReservationDTO> reservations = reservationService.getEventReservations(eventId);
        return ResponseEntity.ok(reservations);
    }
}