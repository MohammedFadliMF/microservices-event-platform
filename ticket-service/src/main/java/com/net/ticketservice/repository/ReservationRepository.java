package com.net.ticketservice.repository;

import com.net.ticketservice.entities.Reservation;
import com.net.ticketservice.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserKeycloakId(String userKeycloakId);

    List<Reservation> findByEventId(Long eventId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByUserKeycloakIdAndEventId(String userKeycloakId, Long eventId);

    @Query("""
        SELECT r 
        FROM Reservation r 
        WHERE r.status = :status 
          AND r.expiryDate < :currentTime
    """)
    List<Reservation> findExpiredReservations(
            @Param("status") ReservationStatus status,
            @Param("currentTime") LocalDateTime currentTime
    );
}