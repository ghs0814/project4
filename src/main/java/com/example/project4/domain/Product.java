package com.example.project4.domain;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Product { //products table
    private Long productId;

    private String name;

    //numeric(12,2)
    private BigDecimal price;


    private Integer stockQuantity;

    private LocalDateTime createdAt;
}
