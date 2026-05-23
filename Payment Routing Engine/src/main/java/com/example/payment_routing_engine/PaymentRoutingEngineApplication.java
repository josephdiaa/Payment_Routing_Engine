package com.example.payment_routing_engine;

import com.example.payment_routing_engine.modules.user.domain.User;
import com.example.payment_routing_engine.modules.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@SpringBootApplication
public class PaymentRoutingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentRoutingEngineApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("password123"));
                admin.setRole("ADMIN");
                admin.setActive(true);
                admin.setCreatedAt(Instant.now());
                admin.setUpdatedAt(Instant.now());
                userRepository.save(admin);
                System.out.println("Default admin user created: admin / password123");
            }
        };
    }

}
