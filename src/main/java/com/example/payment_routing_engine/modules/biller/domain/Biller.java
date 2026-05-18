package com.example.payment_routing_engine.modules.biller.domain;

import com.example.payment_routing_engine.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "biller")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Biller extends BaseEntity {

    @Column(name = "name", nullable = false,unique = true)
    private String name;

    @Column(name = "code",nullable = false,unique = true)
    private String code;

    @Column(name = "active",nullable = false)
    private Boolean active;
}
