package com.net.ticketservice.services;

import com.net.ticketservice.clients.EventServiceClient;
import com.net.ticketservice.dto.*;
import com.net.ticketservice.entities.*;
import com.net.ticketservice.enums.ReservationStatus;
import com.net.ticketservice.enums.TicketStatus;
import com.net.ticketservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final EventServiceClient eventServiceClient;
    private final QRCodeService qrCodeService;

    @Transactional
    public ReservationDTO createReservation(CreateReservationRequest request, String userKeycloakId) {
        EventDTO event = eventServiceClient.getEventById(request.getEventId());

        if (event.getAvailableTickets() < request.getQuantity()) {
            throw new RuntimeException("Not enough tickets available");
        }

        Reservation reservation = new Reservation();
        reservation.setUserKeycloakId(userKeycloakId);
        reservation.setEventId(request.getEventId());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setQuantity(request.getQuantity());
        reservation.setTotalAmount(event.getPrice() * request.getQuantity());
        reservation.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        reservation = reservationRepository.save(reservation);

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < request.getQuantity(); i++) {
            Ticket ticket = new Ticket();
            ticket.setEventId(request.getEventId());
            ticket.setPrice(event.getPrice());
            ticket.setType(request.getTicketType());
            ticket.setStatus(TicketStatus.RESERVED);
            ticket.setReservation(reservation);
            tickets.add(ticketRepository.save(ticket));
        }

        eventServiceClient.updateAvailableTickets(request.getEventId(), -request.getQuantity());

        reservation.setTickets(tickets);
        return convertToDTO(reservation);
    }

    @Transactional
    public ReservationDTO confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getStatus().equals("PENDING")) {
            throw new RuntimeException("Reservation is not in PENDING status");
        }

        if (reservation.getExpiryDate().isBefore(LocalDateTime.now())) {
            cancelReservation(reservationId);
            throw new RuntimeException("Reservation has expired");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);

        for (Ticket ticket : reservation.getTickets()) {
            ticket.setStatus(TicketStatus.SOLD);
            ticketRepository.save(ticket);
            qrCodeService.generateQRCodeForTicket(ticket);
        }

        reservation = reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus().equals("CONFIRMED")) {
            throw new RuntimeException("Cannot cancel confirmed reservation");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        for (Ticket ticket : reservation.getTickets()) {
            ticket.setStatus(TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
        }

        eventServiceClient.updateAvailableTickets(
                reservation.getEventId(),
                reservation.getQuantity()
        );

        reservationRepository.save(reservation);
    }

    public List<ReservationDTO> getUserReservations(String userKeycloakId) {
        return reservationRepository.findByUserKeycloakId(userKeycloakId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return convertToDTO(reservation);
    }

    public List<ReservationDTO> getEventReservations(Long eventId) {
        return reservationRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processExpiredReservations() {

        List<Reservation> expiredReservations = reservationRepository
                .findExpiredReservations(
                        ReservationStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Reservation reservation : expiredReservations) {
            try {
                cancelReservation(reservation.getId());
            } catch (Exception e) {
                // Log error (ex: logger.error("Failed to cancel reservation {}", reservation.getId(), e))
            }
        }
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserKeycloakId(reservation.getUserKeycloakId());
        dto.setEventId(reservation.getEventId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setStatus(reservation.getStatus());
        dto.setQuantity(reservation.getQuantity());
        dto.setTotalAmount(reservation.getTotalAmount());
        dto.setExpiryDate(reservation.getExpiryDate());
        dto.setCreatedAt(reservation.getCreatedAt());

        if (reservation.getTickets() != null) {
            List<TicketDTO> ticketDTOs = reservation.getTickets().stream()
                    .map(this::convertTicketToDTO)
                    .collect(Collectors.toList());
            dto.setTickets(ticketDTOs);
        }

        return dto;
    }

    private TicketDTO convertTicketToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setEventId(ticket.getEventId());
        dto.setPrice(ticket.getPrice());
        dto.setType(ticket.getType());
        dto.setStatus(ticket.getStatus());

        if (ticket.getTicketQR() != null) {
            dto.setQrCode(ticket.getTicketQR().getQrCode());
        }

        return dto;
    }
}
