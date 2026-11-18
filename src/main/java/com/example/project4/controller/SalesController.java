package com.example.project4.controller;

import com.example.project4.dto.SalesSummaryDto;
import com.example.project4.dto.ProductSalesDto;
import com.example.project4.service.SalesService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    // 전체 매출 조회
    @GetMapping("/summary")
    public SalesSummaryDto findSummary() {
        return salesService.findSummary();
    }

    // 상품별 매출 조회
    @GetMapping("/products")
    public List<ProductSalesDto> findProductSales() {
        return salesService.findProductSales();
    }
}