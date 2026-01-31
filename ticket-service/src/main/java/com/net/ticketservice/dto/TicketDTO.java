package com.net.ticketservice.dto;

import com.net.ticketservice.enums.TicketStatus;
import com.net.ticketservice.enums.TicketType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long id;
    private Long eventId;
    private Double price;
    @Enumerated(EnumType.STRING)
    private TicketType type;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    private String qrCode;
}
