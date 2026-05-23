package com.example.payment_routing_engine.modules.usage.repository;

import com.example.payment_routing_engine.modules.usage.domain.GatewayDailyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface GatewayDailyUsageRepository extends JpaRepository<GatewayDailyUsage, UUID> {
    Optional<GatewayDailyUsage> findByGatewayIdAndUsageDate(UUID id, LocalDate date);
}
