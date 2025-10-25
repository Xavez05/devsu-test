package com.bank.customers.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customers")
public class Customer extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String customerId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean status = true;
}
