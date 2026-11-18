package com.example.project4.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//사용자 주문
@Getter
@Setter
public class OrderCreateRequestDto {

    @NotNull
    @Positive
    private Long customerId;

    @NotEmpty //collection
    private List<@NotNull @Valid OrderItemRequestDto> items;
}
//주문 dto와, 주문 상품 dto를 분리해둔 이유
/*
* 실제 1개의 주문서(order)안에 여러개의 주문상품(orderItem) 구조 표현
* 주문서 전체 : 개별 상품 항목
* front단에서 요청시, 이 구조로 계층형 json 보냄
*
* */
