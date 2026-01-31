package com.net.paymentservice.services;

import com.net.paymentservice.dto.*;
import com.net.paymentservice.entities.*;
import com.net.paymentservice.enums.PaymentProvider;
import com.net.paymentservice.enums.PaymentStatus;
import com.net.paymentservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public PaymentDTO processPayment(ProcessPaymentRequest request) {
        // Check if payment already exists
        paymentRepository.findByReservationId(request.getReservationId())
                .ifPresent(p -> {
                    throw new RuntimeException("Payment already exists for this reservation");
                });

        Payment payment = new Payment();
        payment.setReservationId(request.getReservationId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentProcessing(payment, request);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment = paymentRepository.save(payment);
        return convertToDTO(payment);
    }

    private boolean simulatePaymentProcessing(Payment payment, ProcessPaymentRequest request) {
        try {
            Transaction transaction = new Transaction();
            transaction.setPayment(payment);
            transaction.setProvider(PaymentProvider.MOCK);
            transaction.setReference(UUID.randomUUID().toString());
            transaction.setResponseMessage("Payment processed successfully");

            transactionRepository.save(transaction);

            log.info("Payment processed successfully for reservation: {}", payment.getReservationId());
            return true;
        } catch (Exception e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return false;
        }
    }

    @Transactional
    public PaymentDTO refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        Transaction refundTransaction = new Transaction();
        refundTransaction.setPayment(payment);
        refundTransaction.setProvider(PaymentProvider.MOCK);
        refundTransaction.setReference("REFUND-" + UUID.randomUUID().toString());
        refundTransaction.setResponseMessage("Refund processed successfully");
        transactionRepository.save(refundTransaction);

        payment = paymentRepository.save(payment);
        return convertToDTO(payment);
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToDTO(payment);
    }

    public PaymentDTO getPaymentByReservationId(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Payment not found for this reservation"));
        return convertToDTO(payment);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setReservationId(payment.getReservationId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());

        List<TransactionDTO> transactionDTOs = transactionRepository
                .findByPaymentId(payment.getId()).stream()
                .map(this::convertTransactionToDTO)
                .collect(Collectors.toList());
        dto.setTransactions(transactionDTOs);

        return dto;
    }

    private TransactionDTO convertTransactionToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setProvider(transaction.getProvider());
        dto.setReference(transaction.getReference());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setResponseMessage(transaction.getResponseMessage());
        return dto;
    }
}
