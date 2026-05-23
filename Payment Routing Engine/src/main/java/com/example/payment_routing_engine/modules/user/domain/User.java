package com.example.payment_routing_engine.modules.user.domain;

import com.example.payment_routing_engine.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(name = "username",nullable = false,unique = true)
    private String username;

    @Column(name = "password_hash",nullable = false)
    private String passwordHash;

    @Column(name = "role",nullable = false)
    private String role;

    @Column(name = "active",nullable = false)
    private Boolean active;
}
