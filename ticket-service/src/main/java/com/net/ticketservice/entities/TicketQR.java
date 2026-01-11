package com.net.ticketservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TicketQR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketQRID;
    private Long ticketId;
    private String qrCode;
    private LocalDate generatedAt;
}
