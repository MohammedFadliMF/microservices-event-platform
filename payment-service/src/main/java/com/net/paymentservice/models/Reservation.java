package com.net.paymentservice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter @Setter
public class Reservation {
    private Long reservationId;
    private Long userId;
    private Long eventId;
    private LocalDate reservationDate;
    private String status;
}
