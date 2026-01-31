package com.net.eventservice.dto;

import com.net.eventservice.enums.EventStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    private List<Long> categoryIds;
    private List<String> categoryNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
