package com.net.ticketservice.repository;

import com.net.ticketservice.entities.Ticket;
import com.net.ticketservice.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventId(Long eventId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByReservationId(Long reservationId);
    long countByEventIdAndStatus(Long eventId, TicketStatus status);
}
