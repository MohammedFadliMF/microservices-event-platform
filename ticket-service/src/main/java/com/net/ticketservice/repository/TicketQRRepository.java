package com.net.ticketservice.repository;

import com.net.ticketservice.entities.TicketQR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TicketQRRepository extends JpaRepository<TicketQR, Long> {
    Optional<TicketQR> findByQrCode(String qrCode);
    Optional<TicketQR> findByTicketId(Long ticketId);
    boolean existsByQrCode(String qrCode);
}
