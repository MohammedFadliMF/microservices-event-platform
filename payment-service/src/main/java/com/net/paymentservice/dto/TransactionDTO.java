package com.net.paymentservice.dto;

import com.net.paymentservice.enums.PaymentProvider;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;
    private String reference;
    private LocalDateTime createdAt;
    private String responseMessage;
}
