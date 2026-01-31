package com.net.paymentservice.web;

import com.net.paymentservice.dto.*;
import com.net.paymentservice.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process a payment")
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        PaymentDTO payment = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ORGANIZER')")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable Long id) {
        PaymentDTO payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get payment by reservation ID")
    public ResponseEntity<PaymentDTO> getPaymentByReservationId(@PathVariable Long reservationId) {
        PaymentDTO payment = paymentService.getPaymentByReservationId(reservationId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all payments (Admin only)")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
