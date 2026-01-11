package com.net.ticketservice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class Event {
    private Long eventId;
    private String title;
    private String description;
    private LocalDate eventDate;
    private String location;
    private int capacity;
    private Long organizerId;
}
