package com.net.ticketservice.dto;

import com.net.ticketservice.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private String userKeycloakId;
    private Long eventId;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private Integer quantity;
    private Double totalAmount;
    private LocalDateTime expiryDate;
    private List<TicketDTO> tickets;
    private LocalDateTime createdAt;
}
