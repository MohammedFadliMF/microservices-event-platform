package com.net.paymentservice.repository;

import com.net.paymentservice.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPaymentId(Long paymentId);
    Optional<Transaction> findByReference(String reference);
}