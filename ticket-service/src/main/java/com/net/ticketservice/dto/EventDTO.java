package com.net.ticketservice.dto;

import com.net.ticketservice.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private Integer capacity;
    private String organizerKeycloakId;
    private Double price;
    private Integer availableTickets;
    private EventStatus status;
}
