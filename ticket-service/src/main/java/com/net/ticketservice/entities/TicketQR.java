package com.net.ticketservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_qrs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketQR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false, unique = true)
    private Ticket ticket;

    @Column(nullable = false, unique = true, length = 500)
    private String qrCode;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    private boolean isScanned = false;

    private LocalDateTime scannedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
