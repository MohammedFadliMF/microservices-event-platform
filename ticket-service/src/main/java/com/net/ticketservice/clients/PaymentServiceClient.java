package com.net.ticketservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payments/process")
    Boolean processPayment(@RequestParam("reservationId") Long reservationId,
                           @RequestParam("amount") Double amount);
}