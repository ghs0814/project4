package com.example.project4.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SalesSummaryDto { //전체 매출 집계 결과 response

    private Long orderCount;

    private BigDecimal totalSales;
}