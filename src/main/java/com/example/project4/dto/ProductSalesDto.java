package com.example.project4.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSalesDto { //상품별 집계 결과 dto

    private Long productId;

    private String productName;

    private Long totalQuantity;

    private BigDecimal totalSales;
}