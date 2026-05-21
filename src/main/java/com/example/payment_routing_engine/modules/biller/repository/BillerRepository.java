package com.example.payment_routing_engine.modules.biller.repository;

import com.example.payment_routing_engine.modules.biller.domain.Biller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillerRepository extends JpaRepository<Biller, UUID> {
    public Optional<Biller> findByCode(String code);
}
