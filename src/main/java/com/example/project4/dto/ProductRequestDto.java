package com.example.project4.dto;


import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductRequestDto {
    //private Long productId;

    @NotBlank // string
    @Size(max = 100)
    private String name;

    //numeric(12,2)
    @NotNull // string 이외, @NotEmpty는 collection에 사용
    @DecimalMin("0.00") // 0 이상
    @Digits(integer = 10, fraction = 2) // 정수 최대 10자리, 소수 최대 2자리
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    //private LocalDateTime createdAt;
}
