package com.example.project4.mapper;


import com.example.project4.dto.ProductSalesDto;
import com.example.project4.dto.SalesSummaryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalesMapper {

    SalesSummaryDto findSummary(); // 전체 주문 건수 및 매출 조회

    List<ProductSalesDto> findProductSales(); // 상품별 판매 수량 및 매출 조회

    // 고객 & 상품 & 주문 -> 매출 관리
}
