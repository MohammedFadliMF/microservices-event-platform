package com.net.eventservice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class User {
    private Long userId;
    private String username;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDate createdAt;
}
