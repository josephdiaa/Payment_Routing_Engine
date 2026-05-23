package com.example.payment_routing_engine.modules.transaction.repository;

import com.example.payment_routing_engine.modules.transaction.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
    List<PaymentTransaction> findByBillerCodeAndProcessedAtBetween(String billerCode, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
