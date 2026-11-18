package com.example.project4.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Customer { //customers table

    private Long customerId;

    private String name;

    private String email;

    private LocalDateTime createdAt;
}