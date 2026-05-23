package com.example.payment_routing_engine.modules.gateway.repository;

import com.example.payment_routing_engine.modules.biller.domain.Biller;
import com.example.payment_routing_engine.modules.gateway.domain.Gateway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GatewayRepository extends JpaRepository<Gateway, UUID> {
    public Optional<Gateway> findByName(String Name);
}
