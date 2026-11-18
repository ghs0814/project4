package com.example.project4.service;

import com.example.project4.dto.SalesSummaryDto;
import com.example.project4.dto.ProductSalesDto;
import com.example.project4.mapper.SalesMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesMapper salesMapper;

    // 전체 매출 조회
    public SalesSummaryDto findSummary() {
        return salesMapper.findSummary();
    }

    // 상품별 매출 조회
    public List<ProductSalesDto> findProductSales() {
        return salesMapper.findProductSales();
    }
}