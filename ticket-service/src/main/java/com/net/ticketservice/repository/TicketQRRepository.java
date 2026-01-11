package com.net.ticketservice.repository;

import com.net.ticketservice.entities.TicketQR;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketQRRepository extends JpaRepository<TicketQR,Long> {
}
