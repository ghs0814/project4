package com.example.project4.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class Order {
    private Long orderId;

    private Long customerId;

    private String customerName;

    private BigDecimal totalAmount;

    private String status;

    private LocalDateTime createdAt;

    //items
    //상품 목록을 응답에 함께 담기 위해 추가한 필드
    private List<OrderItem> items;

}
